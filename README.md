<img src="icon.png" align="right" width="180px"/>

# Templates 2

[>> Downloads <<](https://modrinth.com/mod/templates-2)

*Slopes?*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. I appreciate hearing about people using my mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

Templates 2 adds themeable blocks.

* If a Template block is placed in the world and right-clicked with a full-size block, it will take on the appearance of that block, including whether it emits light and redstone.
* Adding a *redstone torch* will make them emit redstone power, *glowstone dust* will make them emit light, and *popped chorus fruit* will make them intangible.

Templates adds a handful of common shapes, but it's not too hard for other mods to interface with Templates and add their own templatable blocks.

# For addon developers

## Creating a block entity

All templates need a block entity to store what block they look like. Templates registers one under `templates:slope`. As an addon developer, you should not hack additional blocks onto this block entity.

However, nothing in Templates relies on the *specific* block entity ID. The only hard requirement is that it `implements ThemeableBlockEntity`. (This implies it returns a `BlockState` from `getRenderAttachmentData`.)

You may return `TemplateEntity` or create a subclass of it.

## Creating your block

There are various block interactions in Templates, like adding redstone to make the block emit power. To make your block fit with the other Templates, you'll want those behaviors to apply to your block as well.

* If the original block is a `Block` or simple vanilla class like `WallBlock`, register the corresponding class in `io.github.cottonmc.templates.block`.
* Otherwise, have a look at `TemplateInteractionUtil`, which contains implementations of all the necessary interactions. Forward the appropriate Block calls to those methods.
* Or don't bother. None of this is important for the actual *retexturing* part of the mod.

## Creating the custom model

(TL;DR look at `assets/templates/blockstates` and the bottom of `TemplatesClient`)

Of course Templates leverages custom baked models. All of Templates's baked model implementations find and retexture quads from an upstream model that you will need to provide.

Templates tries hard to retain the orientation of blocks placed inside of them - you can place specifically an *east-facing* log into a Template, for example, and east/west faces get the cut wood while the other faces get the bark. To that end, Templates needs three pieces of information to perform retexturing:

* the quad to be retextured
* whether you actually want to retexture it or just pass it through unchanged (ex. Lever Template, which doesn't change the lever arm);
* what face of the block it corresponds to (which is sometimes different from "the direction it points" - see the Door Template, the template rotates with the door)

Templates provides three model implementations designed for other mods to use.

### Auto retexturing

Construct with `TemplatesClientApi.getInstance().auto`. Pass the ID of the JSON model you want to source quads from.

All quads will be retextured. Templates will guess the facing direction from the direction the face points (or the cullface, if one is set)

This implementation doesn't work well with `multipart` models that have differently-rotated parts.

### Special texture-based retexturing

Construct with `TemplatesClientApi.getInstance().json`. Pass the ID of the model you want to source quads from. All quads textured with the *special textures* `templates:templates_special/east` will be textured with the east side of the theme, all quads textured with `templates:templates_special/up` will be retextured with the top side of the theme, etc. Quads textured with any other texture will be passed through unaltered.

<details><summary>Regarding texture variables:</summary>

On the off-chance your blockmodel already has texture variables for `north`, `south`, etc, you can simply apply Templates's special textures to it:

```json
{
	"parent": "mymod:block/my_model",
	"textures": {
		"north": "templates:templates_special/north",
		"east": "templates:templates_special/east",
		"south": "templates:templates_special/south",
		"west": "templates:templates_special/west",
		"up": "templates:templates_special/up",
		"down": "templates:templates_special/down",
	}
}
```

Many models don't specify *completely* separate textures for all six sides, instead using variables like "ends" for a texture to apply to the top *and* bottom faces. Please use a model which specifies all six faces individually. 
</details>

(This one works better with multipart models.)

### Mesh retexturing

Construct with `TemplatesClientApi.getInstance().mesh`, passing a `Supplier<Mesh>`. To mark a face as "retexture this with the EAST side of the theme", call `.tag(Direction.EAST.ordinal() + 1)` on it; same for the other directions. Give these faces UV coordinates ranging from 0 to 1 -- Templates will take care of scaling them onto the appropriate texture. The valid tags are 1, 2, 3, 4, 5, and 6, corresponding to down, up, north, south, west, and east.

A `.tag` of 0 (the default) will be passed through unchanged. Instead of passing a `Supplier<Mesh>` you may pass a `Function<Function<SpriteIdentifier, Sprite>, Mesh>`; your function will receive a `Function<SpriteIdentifier, Sprite>` which can be used to find sprite UVs for these faces.

(To construct this type, you will also need to pass the identifier of a "base model", which can be a regular JSON model. Miscellaneous `BakedModel` properties like rotations, AO, `isSideLit`, etc will be sourced from it. See Template's `models/block/slope_base`. You may need to set `"gui_light": "front"` to avoid a flat look in the ui.)

### A secret, fourth thing

All of Template's model classes are considered public API and you may extend them. Also consider extending `RetexturingBakedModel`. You may also supply your own unbaked models (of course, it won't *work* unless you reimplement the retexturing).

## Registering your model

After you've decided on and constructed your special model, you should tell Templates about it.

Pick an ID that's different from the base model. (If your base model is `mymod:block/awesome_template`, a good name might be `mymod:awesome_template_special`). Register your special model under that ID using `TemplatesClientApi.getInstance().addTemplateModel`.

To assign the block model, using a vanilla blockstate file, simply point your block at that model ID as normal. (See this mod's `blockstates` folder.) You may also use the `x`, `y`, and `uvlock` properties. To assign the item model, since items don't have the "blockstate file" level of indirection, call `TemplatesClientApi.getInstance().assignItemModel`, passing your special model's ID and the items it should be assigned to. Or if you'd rather use a vanilla json model that won't be retextured, just make one the vanilla way.

# Most important attribution in the whole wide world

COOL RIVULET is by mev, this is the most important block in the mod & perhaps the most important block in any mod ever since `incorporeal:clearly`

# License

MIT
