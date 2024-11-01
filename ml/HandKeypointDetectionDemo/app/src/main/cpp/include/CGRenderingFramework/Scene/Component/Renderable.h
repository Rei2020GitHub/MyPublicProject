/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: Renderable utils.
 * Author: xuhan
 * Create: 2020-3-1
 */

#ifndef RENDERABLE_H
#define RENDERABLE_H

#include "Scene/IComponent.h"
#include "Math/AABB.h"
#include "Math/Matrix4.h"

NS_CG_BEGIN

class SceneObject;
class Mesh;
class Material;

class CGKIT_EXPORT Renderable : public IComponent {
    friend class SceneObject;

protected:
    Renderable(SceneObject* pSceneObject = nullptr);
    virtual ~Renderable();

public:
    virtual void Render(const Matrix4& transMat);

    void SetMesh(const Mesh* pMesh);
    const Mesh* GetMesh() const;

    void SetMaterial(u32 index, const Material* material);

    void UpdateAABB(const Matrix4& transMat);

    const AABB& GetAABB() const
    {
        return m_aabb;
    }

private:
    void Destroy();

protected:
    Mesh* m_mesh = nullptr;
    std::vector<Material*> m_materials;
    AABB m_aabb;
};

NS_CG_END

#endif