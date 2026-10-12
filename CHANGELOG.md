# Changelog

All notable changes to Beacon Range are documented here.

## [1.1.0] - 2026-10-12

### Added

- Added the **Toggle Beacon Visuals** keybind to Minecraft's Controls menu.
- Added an action-bar notification when beacon visuals are turned on or off.
- Added configurable keybind support with **F8** as the default key.
- Added terrain-aware vertical range rendering.
- Added unit tests for beacon surface detection, range edges, clearance, and
  build-height limits.

### Changed

- Beacon boxes now extend up to 50 blocks above the highest terrain surface
  within the beacon's horizontal range.
- Beacon box height is capped at the dimension's build height.
- Moved the toggle keybind into Minecraft's built-in **Miscellaneous** category.
- Renamed the mod ID and resource namespace from `modid` to `beaconrange`.
- Replaced the template icon with the Beacon Range icon.
- Updated mod metadata, description, author, and repository links.

### Fixed

- Fixed the visibility toggle using a hard-coded F8 key instead of a
  rebindable Minecraft key mapping.
- Fixed the resource namespace and mixin configuration names still using
  starter-template values.

### Compatibility

- Minecraft 26.2
- Fabric Loader 0.19.5 or newer
- Fabric API
- Java 25 or newer
