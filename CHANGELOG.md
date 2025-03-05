# 2.3.0 (unreleased)

* **Potential ABI break**: Use `fabric-block-view-api-v2` instead of the deprecated `fabric-rendering-data-attachment-v1`. This means I use `RenderDataBlockEntity` instead of the deprecated `RenderAttachmentBlockEntity` to read information about templates from the world.
  * *If you implement `ThemeableBlockEntity`*, please implement `ThemeableBlockEntity2` instead. However, the old interface has been changed to extend the new interface and forward calls from `fabric-rendering-data-attachment-v1`, so mods **should still work**.

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