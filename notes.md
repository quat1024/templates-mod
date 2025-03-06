On migrating from `fabric-models-v0` to `fabric-model-loading-api-v1`.

* old ModelLoadingRegistry has 3 extension points:
  * `registerModelProvider` for loading additional models (same as deprecated `registerAppender`)
  * `registerResourceProvider` for resolving Identifiers to UnbakedModels in a custom way
  * `registerVariantProvider` for resolving ModelIdentifiers to UnbakedModels in a custom way

You don't need `registerModelProvider`/`Appender` unless you want to load a blockmodel not referenced by any block or item. Resource and variant providers are "hot" code since loading basically any model in the game calls the resource and variant providers.

quick refresher: modelidentifiers have the `#inventory` bit at the end, generally these are used by item models and blockstate discriminators

The new system: we have `ModelLoadingPlugin`s.

* On resource load it calls `onInitializeModelLoader` with a context. You can ask the context to:
  * register aditional models (Identifier or ModelIdentifier) to load
    * Similar to `registerModelProvider`
  * register a custom block -> unbakedmodel resolver for a given block
  	* welcome back 1.12
  * an ModelResolver event, which provides your `Identifier/ModelIdentifier` -> `UnbakedModel` function
    * similar to `registerResourceProvider`
  * events for modifying models while it's loaded and pre/postbaked

NOT GOOD: the ModelResolver event is not called for `ModelIdentifier`s.

`modifyModelBeforeBake` mentions that it's useful for "wrapping a block's model into a non-JsonUnbakedModel class but still allowing the item model to be loaded and baked without exceptions". Interesting. (basically in vanilla you can't have json models extend non-json models, and all item models are loaded as json models.)

`modifyModelBeforeBake` is also called for ModelIdentifiers. So you can replace any model (including item models) using mmbb. However the model loading process still happens underneath you

## Tldr

* If you have a resource reload hook specifically for dumping a cache, you can remove it and dump your cache in `onInitializeModelLoader`
  * If you actually access the resource loader, there is a `PreparableModelLoadingPlugin` 
* Move your `registerResourceProvider` under the model resolver event

If you are outright replacing item models:

* Use `modifyModelBeforeBake`. `context.id` might actually be a `ModelIdentifier` so look for the `#inventory` ones.
* Return a different UnbakedModel as appropriate.
* Since this happens at a later point in the model loading process: you might now get logspam about missing `#inventory` models. Sadly you'll have to datagen some sacrificial models I think