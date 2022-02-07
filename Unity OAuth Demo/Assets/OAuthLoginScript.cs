using System;
using System.Threading.Tasks;
using UnityEngine;
using UnityEngine.UI;

namespace Assets
{
    public class OAuthLoginScript : MonoBehaviour
    {
        public Text LogText;

        private readonly UnityAuthClient _authClient = new UnityAuthClient();
        private bool _signedIn;

        public async void OnClickGoogleIdLoginButton()
        {
            Debug.Log("OAuthLoginScript::OnClickGoogleIdLoginButton");

            LogText.text = "Sign in Google ID";

            _signedIn = await _authClient.LoginAsync(
                "https://accounts.google.com/", 
                "687772335311-90q848m74kpdlqnuvr6t98jicfj9rgek.apps.googleusercontent.com", 
                "openid profile email", 
                "com.defaultcompany.unityoauthdemo:/callback", 
                "com.defaultcompany.unityoauthdemo:/callback"
            );

            if (_signedIn)
            {
                LogText.text = "Open Id : " + _authClient.GetPayload("sub");
                LogText.text += "\n";
                LogText.text += "Name : " + _authClient.GetPayload("name");
                LogText.text += "\n";
                LogText.text += "Family Name : " + _authClient.GetPayload("family_name");
                LogText.text += "\n";
                LogText.text += "Given Name : " + _authClient.GetPayload("given_name");
                LogText.text += "\n";
                LogText.text += "Picture : " + _authClient.GetPayload("picture");
                LogText.text += "\n";
                LogText.text += "Email : " + _authClient.GetPayload("email");
                LogText.text += "\n";
                LogText.text += "Email Verified : " + _authClient.GetPayload("email_verified");
                LogText.text += "\n";
                LogText.text += "Access Token : " + _authClient.GetAccessToken();
                LogText.text += "\n";
                LogText.text += "Refresh Token : " + _authClient.GetRefreshToken();
                LogText.text += "\n";
                LogText.text += "Id Token : " + _authClient.GetIdToken();
            }
            else
            {
                LogText.text = "An error occurred during sign in. Please ensure you have Internet access.";
            }
        }

        public void OnAuthReply(object value)
        {
            _authClient.Browser.OnAuthReply(value as string);
        }
    }
}