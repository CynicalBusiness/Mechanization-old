Mechanization Changelog
====
Alpha Versions
----
### 1.00.00
- `ADD` Output logs now have color.
- `FIX` The plugin now builds XML without combusting.
- `FIX` Data wildcards for activators now function.

### 1.01.00
- `MOD` Massive change to the plugin: Now builds on the EntropyAPI instead of the deprecated XMLAPI.
- `MOD` The `items.xml` file has drastically changed for use with the EntropyAPI. It will continue to change in the future.
- `MOD` The input/output of a recipe has been merged to one `shape` element.
- **1.01.01**
  - `FIX` Some log messages were displaying the wrong information.

### 1.02.00
- `ADD` Material keys now have an OR operator.
- `ADD` All material values can now be wildcards, not just in the factory matrix.
- `MOD` The code is now considerably cleaner and faster.
- `MOD` Material-type attributes now support data directly instead of having a separate attribute.
- `MOD` The recipe `fuel_cost` attribute has moved from `meta` to `shape`.
- `MOD` The factory `recipe` attribute was moved from `meta` to `data`.
- `FIX` Activators now correctly check for data.
- `DEL` The factory `color` attribute was removed. The `display_name` attribute now parses color.
- `DEL` The data attribute has been removed in all instances in favor of the latter mentioned material system.
- `DEL` A lot of the comments were removed from XML files and put on the wiki.
- **1.02.01**
  - `FIX` Replaced the EntropyAPI with a different version.
  - `FIX` JDOM issues have been resolved. (I hate bukkit's class loader)
- **1.02.02**
  - `FIX` Large code rework because the EntropyAPI was not effectively communicating.
  - `FIX` Fixed a strange comparison in the recipe parser.
  - `FIX` Fixed a derp with COBBLE_WALL. For some reason, the data was 2 instead of 0.
- **1.02.03**
  - `FIX` Fixed factories sometimes setting the chest's inventory to the input when finished.
  - `FIX` Fixed the `steel` item not correctly loading from the EntropyAPI.
- **1.02.04**
  - `FIX` Changed the meta `display` property to `display_name`. The display name now correctly loads.
  - `FIX` The *Saw Mill* now has correct dimensions.
  - `FIX` The *Gem Smelter*'s `name` attribute was fixed.
  - `FIX` The *Kiln* and *Gem Smelter* now actually load recipes.
  - `FIX` The *Kiln*, *Saw Mill*, and *Gem Smelter* now correctly display colors.
- **1.02.05**
  - `ADD` Missing custom items or recipes will now result in `WARN` message.
  - `MOD` If something fails to load completely, it now raises an `ERR` message instead of `WARN`.
  - `FIX` Fixed the recipe `display_name` property.
- **1.02.06**
  - `ADD` Added some line breaks in the start-up logs.
  - `ADD` The plugin now outputs a rule when it has finished enabling.
