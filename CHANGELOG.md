
Yeah i'm going cherry-picking so the changelog is becoming a mess.

# 2.5.0 (1.21.1) (Mar 08 2025 but later in the day)

* Rewrite a lot of the retexturing logic.
  * Now, only one ready-to-retexture `Mesh` is used per bakedmodel, which is probably how it should have been in the first place.
  * Should save memory and improve chunk-baking performance a little bit.
  * There are several ABI breaks related to this. In `io.github.cottonmc.templates.api` (the "official API"):
    * `getOrCreateTemplateApperanceManager` has been removed. The global template appearance manager has been removed.
    * `TweakableUnbakedModel.itemModelState` has been stubbed and is no longer necessary to call.
    * Let me know if you need more information
* Permute the faces of fewer types of Templates. (This might rotate blocks inside some Templates.)

# 2.4.1 (1.21.1) (Mar 08 2025)

* Remove mixin that did nothing and broke Sinytra Connector
* Tag template button with `wooden_buttons` again
* Load nbt tag clientside (preventing flicker of regular template when you place one)

# 2.4.1 (1.20.4) (Mar 08 2025)

* Remove mixin that did nothing and (probably) broke Sinytra Connector
* Tag template button with `wooden_buttons` again

# 2.3.2 (1.20.1) (Mar 08 2025)

* Remove mixin that did nothing and (probably) broke Sinytra Connector
* Tag template button with `wooden_buttons` again

# 2.4.0 (1.21.1) (Mar 07 2025)

Port to 1.20.1

# 2.4.0 (1.20.4) (Mar 07 2025)

Port to 1.20.4

# Changelog before it became a mess of nonlinear time

# 2.3.1 (Mar 06, 2025, but in the evening)

* Add recipes to craft Slab Templates into Vertical Slab Templates and vice versa.
* If Templates can't decide on a texture, it'll now fall back to the block's particle texture, instead of displaying the glitchy scaffolding texture.

# 2.3.0 (the wee hours of Mar 06, 2025)

* Fix messed up Fence Template and Post Template items from last update.
* Add a tooltip to the Slope and Tiny Slope templates reminding you that they can be placed sideways on walls by holding Shift.
* Retexturable models (for "json" and "auto" types), as well as item model overrides, can now be loaded out of a resource pack.
  * See the json files lurking in `assets/templates/`. 
  * This isn't *too* useful, since the models only make *sense* when applied to a block from Templates.
  * The existing code-registration API still works. These are dubbed "permanent" mappings (since they aren't cleared on resource reload).
  * Permanent mappings can be overridden with resource-pack ones.
* More datagen.
* **Potential ABI break**: Use `fabric-block-view-api-v2` instead of the deprecated `fabric-rendering-data-attachment-v1`.
	* This means I use `RenderDataBlockEntity` instead of the deprecated `RenderAttachmentBlockEntity` to read information about templates from the world.
	* *If you implement `ThemeableBlockEntity`*, please implement `ThemeableBlockEntity2` instead. However, the old interface has been changed to extend the new interface and forward calls from `fabric-rendering-data-attachment-v1`, so addons **should still work**.
* **Potential ABI break**: Use `fabric-model-loading-api-v1` instead of the deprecated `fabric-models-v0`.
	* Due to shortcomings of this API (no ability to directly set an `UnbakedModel` for items), Template items now need an arbitrary json item model to suppress "missing model" logspam.
  * In the future I intend to make fuller use of the features afforded by this library.

# 2.2.1 (Mar 01, 2025)

* People were rightfully wary of my sketchy TNT data-tracker mixin, so I put a priority on it. It should hopefully apply in a consistent ordering and not cause problems.
  * **Please remember to apply this update to the client AND server**
* TNTemplate now feels more like a tnt block (breaks instantly, etc)
* Fix recipe unlocking! It now only unlocks when you get bamboo for the first time. Nobody noticed.
* A significant portion of the mod's assets and data files are now automatically generated. Let me know if something feels off.

# 2.2.0 (Feb 25, 2025)

Old changes (~2023, unreleased until now):

* Start sketching out an API, accessible through `TemplatesClientApi.getInstance()`
  * (Note from the future: This API will break *hard* in 1.21 due to Mojang fuckery)
* Code cleanups that hopefully didn't break ABI compat
* Remove some unused stuff from the jar
* Vertical slab placement is a little better
* Fix a bug where templates that look like blocks with randomized models, such as stone, could reroll their blockstate on every resource load
  * Forgot to specify a random seed.
  * Templated blocks always use the *same* model -- templated stone will still not be randomly rotated/flipped -- but at least now it uses the *same* same model.

New changes:

* New template: "Post Cross" - a plus shape that fits squarely on fences or the Post template. Can be placed in three different orientations.
* New template: "TNTemplate". This will end well.
* Connected textures should work! I tested with Chisel Reborn. Other blocks should be OK as long as they implement `FabricBlock#getAppearance`.
* Mushroom blocks now work (as well as any other blocks with `"multipart"` models).
* Very slightly reduce memory usage of `TemplateAppearance`?
* Only declare dependencies on the Fabric API modules I actually use.

TODO list:

* I still depend on two deprecated modules, `fabric-rendering-data-attachment-v1` (has ABI implications) and `fabric-models-v0` (the replacement seems to be `fabric-model-loading-api-v1`, and this also has ABI implications)

# 2.1.1 (Aug 2, 2023)

Enable ambient-occlusion ("smooth lighting") on all Templates except for the slopes, which are still bugged

# 2.1.0 (Jul 31, 2023)

* Add a vertical slab template
* Add a "tiny slope" template
* Change the block entity NBT format to be much smaller
* Reduce memory footprint of the block entity
* Respect `doTileDrops`
* Improve creative ctrl-pick behavior on glowing Templates
* Adding a Barrier block to a Template makes it remove its model (not unbreakable)

# 2.0.4 (Jul 25, 2023)

* Apply more block tags
* Apply item tags

# 2.0.3 (Jul 23, 2023)

* add Door and Iron Door templates
* cool rivulet

# 2.0.2 (Jul 20, 2023)

* Add an Iron Trapdoor template
* Add some more mod metadata (change name to "Templates 2", add authors, fix sources link)

# 2.0.1 (Jul 11, 2023)

Fix a duplication glitch with the Stair Template, which was retaining its block entity after being broken.

# 2.0.0 (Jul 11, 2023)

Initial release