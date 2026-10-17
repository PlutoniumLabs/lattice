# Changelog
## 1.4.0 — 2026-10-17

### Added

- Updated the mod for Minecraft 26.3 on Fabric and NeoForge.
- Added `/requestforceload add|query|remove` with a configurable per-player chunk limit. Requests use block coordinates, track ownership per dimension and count chunks across dimensions.
- Added `enableHomeCommand`, `enableNickCommand`, and `enableStatusCommand` config options, enabled by default.
- Added a command logger and a mod icon.

### Changed

- Removed the rules feature, which is now provided by the server.
- Removed the client-supplied `origin` value from mod-sharing packets to prevent impersonation.
- Switched Discord webhook payload construction to JSON.

### Fixed

- Save force-load ownership when requests change and migrate older ownership entries to the Overworld.
- Prevent players from removing chunks they did not request and from removing ownership in another dimension.
- Skip Discord webhook sends when no webhook is configured.
- Corrected German home messages and updated force-load translations and documentation.
- Fixed the closed-dimension bypass and made the custom dispenser behavior configurable.

[Commits since 1.3.2](https://github.com/PlutoniumLabs/lattice/compare/v1.3.2...4ac9c77)

## 1.3.2 — 2025-12-14

- Added home commands, vanilla mode, Discord webhook chat integration and dispenser durability configuration.
- Added a glow effect to the Ender Dragon egg dropped in the End.
- Improved Discord RPC stability and changed its IPC integration.
- Fixed home ownership checks, client packet registration and vanilla-mode registration.

[Commits since 1.3.1](https://github.com/PlutoniumLabs/lattice/compare/v1.3.1...v1.3.2)

## 1.3.1 — 2025-11-16

- Added Discord Rich Presence with player-head artwork and disconnect handling.

[Commits since 1.3.0](https://github.com/PlutoniumLabs/lattice/compare/v1.3.0...v1.3.1)

## 1.3.0 — 2025-11-12

- Updated to Minecraft 1.21.10.
- Added rules acceptance, PvP administration, a Nether opening date, a help command and an allowed-mod list.
- Improved mod and resource-pack reporting, command management, packets and hoppers.

[Commits since 1.2.1](https://github.com/PlutoniumLabs/lattice/compare/v1.2.1...v1.3.0)

## 1.2.1 — 2025-10-05

- Fixed Fabric compatibility and a disconnect issue.

[Commits since 1.2.0](https://github.com/PlutoniumLabs/lattice/compare/v1.2.0...v1.2.1)

## 1.2.0 — 2025-10-05

- Added rotating MOTDs and improved mod and resource-pack detection after pack reloads.
- Moved nickname and status settings into per-player options.

[Commits since 1.1.0](https://github.com/PlutoniumLabs/lattice/compare/v1.1.0...v1.2.0)

## 1.1.0 — 2025-09-30

- Added player status management and a status decorator.
- Added End portal opening controls and messages for the closed End.

[Commits since 1.0.0](https://github.com/PlutoniumLabs/lattice/compare/v1.0.0...v1.1.0)

## 1.0.0 — 2025-09-28

- Initial release with server configuration, nickname management and illegal-mod detection.

[Initial history](https://github.com/PlutoniumLabs/lattice/commits/v1.0.0/)
