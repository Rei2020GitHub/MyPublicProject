using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using IdentityModel.Client;
using IdentityModel.OidcClient;
using IdentityModel.OidcClient.Browser;
using IdentityModel.OidcClient.Infrastructure;
using IdentityModel.OidcClient.Results;
using UnityEngine;

namespace Assets
{
    public class UnityAuthClient
    {
        private OidcClient _client;
        private LoginResult _result;
        private JwtSecurityTokenHandler jwtSecurityTokenHandler = new JwtSecurityTokenHandler();
        private JwtSecurityToken jwtSecurityToken;


        public UnityAuthClient()
        {
            Browser = new AndroidChromeCustomTabBrowser();
            CertificateHandler.Initialize();
        }

        private OidcClient CreateAuthClient(string authorityUrl, string clientId, string scope, string redirectUri, string postLogoutRedirectUri)
        {
            var options = new OidcClientOptions()
            {
                Authority = authorityUrl,
                
                ClientId = clientId,
                
                Scope = scope,
                RedirectUri = redirectUri,
                PostLogoutRedirectUri = postLogoutRedirectUri,
                ResponseMode = OidcClientOptions.AuthorizeResponseMode.Redirect,
                Flow = OidcClientOptions.AuthenticationFlow.AuthorizationCode,
                Browser = Browser,
            };
            options.Policy.Discovery.ValidateIssuerName = false;
            options.Policy.Discovery.ValidateEndpoints = false;

            return new OidcClient(options);
        }

        public async Task<bool> LoginAsync(string authorityUrl, string clientId, string scope, string redirectUri, string postLogoutRedirectUri)
        {
            _client = CreateAuthClient(authorityUrl, clientId, scope, redirectUri, postLogoutRedirectUri);
            try
            {
                _result = await _client.LoginAsync(new LoginRequest());
                jwtSecurityToken = jwtSecurityTokenHandler.ReadJwtToken(_result.IdentityToken);
            }
            catch (Exception e)
            {
                Debug.Log("UnityAuthClient::Exception during login: " + e.Message);
                return false;
            }
            finally
            {
                Debug.Log("UnityAuthClient::Dismissing sign-in browser.");
                Browser.Dismiss();
            }

            if (_result.IsError)
            {
                Debug.Log("UnityAuthClient::Error authenticating: " + _result.Error);
            }
            else
            {
                Debug.Log("UnityAuthClient::AccessToken: " + _result.AccessToken);
                Debug.Log("UnityAuthClient::RefreshToken: " + _result.RefreshToken);
                Debug.Log("UnityAuthClient::IdentityToken: " + _result.IdentityToken);
                Debug.Log("UnityAuthClient::Signed in.");
                return true;
            }

            return false;
        }

        public async Task<bool> LogoutAsync()
        {
            try
            {
                await _client.LogoutAsync(new LogoutRequest() {
                    BrowserDisplayMode = DisplayMode.Hidden,
                    IdTokenHint = _result.IdentityToken });
                Debug.Log("UnityAuthClient::Signed out successfully.");
                return true;
            }
            catch (Exception e)
            {
                Debug.Log("UnityAuthClient::Failed to sign out: " + e.Message);
            }
            finally
            {
                Debug.Log("UnityAuthClient::Dismissing sign-out browser.");
                Browser.Dismiss();
                _client = null;
            }

            return false;
        }

        public string GetPayload(string payload)
        {
            return jwtSecurityToken == null ? "" : jwtSecurityToken.Payload[payload]?.ToString();
        }

        public string GetAccessToken()
        {
            return _result == null ? "" : _result.AccessToken;
        }

        public string GetRefreshToken()
        {
            return _result == null ? "" : _result.RefreshToken;
        }

        public string GetIdToken()
        {
            return _result == null ? "" : _result.IdentityToken;
        }


        public MobileBrowser Browser { get; }
    }
}
