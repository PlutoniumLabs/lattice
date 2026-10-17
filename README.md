# Lattice

Lattice is a Minecraft mod intended to be used on SMP servers to manage many useful features for players.  
For it to work, it needs to be installed on the server and the client.

See the [changelog](CHANGELOG.md) for release history.

## Features

- Player nickname and status management
- Night vision toggle for players
- Server configuration management (save/reload)
- Message of the Day (MOTD) rotation
- Detection of illegal/cheat mods [Unavailable in Vanilla mode]
- Decorators for enhanced in-game experience (e.g., time/status decorators)
- Configurable PvP settings per player
- Server, end and nether opening dates
- Server configurable Discord RPC [Unavailable in Vanilla mode]
- Discord bridge for chat messages
- Player-requested force-loaded chunks with a configurable per-player limit

## Commands

Below is a list of all available commands and their functionality:

### `/nv`

- **Toggles Night Vision**

### `/nick [nickname]`

- **Sets or removes your nickname**
- If you pass your actual Minecraft name as the nickname, your nickname is removed
- If called without arguments, shows your current nickname

### `/status [<status>|reset]`

- **Changes your status message**
- If you pass `reset` as the status, your status is removed
- If called without arguments, shows your current status message

### `/home <name>`, `/sethome <name>`, `/delhome <name>`

- **Commands for managing your homes**
- Teleport to, create or delete one of your homes

### `/requestforceload <add|query|remove> <x> <z>`

- **Allows players to force load chunks**
- Force-load, query or remove the chunk containing the given block coordinates in your current dimension
- Relative coordinates such as `~ ~` use your current position
- You can remove only chunks you requested; the per-player limit counts chunks across all dimensions

### `/lattice <action> [subAction] [subActionArg]`

- **Performs administrative actions** (requires permission level 2)
- Available actions:
    - `config` - Manage server configuration
        - `save` - Saves the current configuration to disk
        - `reload` - Reloads the configuration from disk
    - `motd` - Manage Message of the Day
        - `add` - Adds a new MOTD
        - `remove` - Removes a MOTD
        - `list` - Lists all MOTDs
  - `<illegal|allowed>` - Manage illegal and allowed mods
      - `add` - Adds a new mod to the list
      - `remove` - Removes a mod from the list
      - `list` - Lists all mods in the list
    - `pvp` - Manage PvP settings
        - `<player>` - Specifies the player
            - `<enable|disable>` - Enables or disables PvP for the specified player
    - `open` - Manage server and end opening dates
        - `<dimension>` - Specifies what to modify [nether, end, server]
            - `<date>` - Sets the opening date in the format `YYYY-MM-DD hh:mm:ss`
    - `nick` - Manage nickname for a player
        - `<player>` - Specifies the player
            - `remove` - Removes the nickname for a player
        - `<nickname>` - Sets the nickname for a player
    - `status` - Manage status messages for a player
        - `<player>` - Specifies the player
            - `remove` - Removes the status message for a player
            - `<status>` - Sets the status message for a player
    - `help` - Shows a link to the repository
- Examples:
    - `/lattice config save`
    - `/lattice motd add Welcome to the server!`
    - `/lattice illegal list`
    - `/lattice pvp PlayerName enable`
    - `/lattice open end 2024-12-31 23:59:59`
    - `/lattice nick PlayerName remove`
    - `/lattice status PlayerName COOL`

## Configuration

- Config file is stored as `config.json` under lattice in the server config directory
- `enableHomeCommand`, `enableNickCommand`, and `enableStatusCommand` independently enable the corresponding player commands. The nick and status switches also disable their `/lattice` administrative actions. All three default to `true`. Edit the file and run `/lattice config reload` to apply changes; reconnect to refresh command suggestions.
- `maxForceloadedChunks` sets the per-player force-load limit across all dimensions (default: `10`)
- You can change the time when the end or the server will be opened
- MOTDs and a list of illegal mods can be configured
- Each player has configurable options:
    - PvP enabled/disabled
    - Nickname
    - Status message
  - List of homes

## Enabling Vanilla mode

1. Shut down the server
2. Open the `config/lattice/config.json` file
3. Add `"vanillaMode": true,` to the file
4. Start the server
5. Great success!

## Known issues

- The hopper sort doesn't work with lithium's hopper optimizations enabled. Please disable them in the lithium config if you want to use hopper sorting. Add this to config/lithium.properties:

```
mixin.block.hopper=false
```
