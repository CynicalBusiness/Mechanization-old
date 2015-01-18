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
