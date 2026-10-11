# Beacon Range

**Beacon Range** is a client-side Fabric utility mod for visualizing beacon
coverage while building and planning your base.

## Current features

- Automatically detects active beacons in nearby loaded chunks.
- Displays each beacon's effective area as a transparent range box.
- Uses a separate color for each beacon to make overlapping coverage easier to
  distinguish.
- Open a beacon's GUI to access the **Range: ON/OFF** button for that specific
  beacon.
- Use **Range Color** to choose a custom color for the selected beacon.
- Press **F8** to quickly hide or show all range visualizations.
- Saves beacon colors, individual beacon visibility, and the F8 visibility
  setting between sessions.
- Supports Minecraft 26.2 with Fabric Loader and Fabric API.

Beacon range boxes represent the beacon's horizontal range of 20, 30, 40, or
50 blocks depending on its pyramid level. The vertical display is capped to
keep the visualization useful while building instead of filling the entire
height of the world.

## Planned features

- A dedicated pinning system so selected beacons remain visible after leaving
  their nearby loaded area.
- Chat commands for listing, removing, and clearing pinned beacons.
- Tab completion for pinned beacon coordinates.
- Additional improvements to overlapping-range visualization and configuration.

This project is being actively developed. Features and behavior may change as
the mod continues to evolve.

Requires **Fabric Loader** and **Fabric API**.

Artificial intelligence has been used to help create this mod.
