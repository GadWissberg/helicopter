package com.gadarts.helicopter.core.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.g3d.Model
import java.util.Locale.ROOT

enum class ModelsDefinitions : AssetDefinition<Model> {
    APACHE,
    PROPELLER,
    BACK_PROPELLER, ;

    private var path: String = "models/${name.toLowerCase(ROOT)}.g3dj"

    override fun getPath(): String {
        return path
    }

    override fun getParameters(): AssetLoaderParameters<Model>? {
        return null
    }

    override fun getClazz(): Class<Model> {
        return Model::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }
}