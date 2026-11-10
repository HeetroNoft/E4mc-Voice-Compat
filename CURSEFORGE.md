![E4MC Voice Compatibility Title](https://media.forgecdn.net/attachments/description/null/description\_9603bf2d-2684-4b9f-8391-5cc7bad3737f.png)

Makes **Simple Voice Chat** work for players who join your world through **e4mc**.

## The problem

e4mc opens your LAN world to anyone on the internet by tunneling the Minecraft connection through a relay. Simple Voice Chat sends audio over a separate UDP port, which e4mc doesn't forward. Your friends can join and play, but their voice chat stays disconnected.

## The fix

This mod sends voice through the Minecraft connection itself, so it goes through the e4mc tunnel like the rest of the game.

- Works with both e4mc connection modes: the **relay** (`*.e4mc.link` address) and **Dialtone**.
- The **host** and players on your **local network** keep using regular voice chat. Nothing changes for them.
- A player who joins through e4mc **without** this mod can still play, and gets a chat message explaining why voice chat doesn't work for them.
- No configuration needed.

## Installation

Download the file that matches your Minecraft version, and install it on **the host and every player**, alongside its dependencies.

Then open your world to LAN as usual, share the e4mc address, and voice chat connects on its own.

## Requirements

Available for **Minecraft 1.20.5 to 26.3**, on Fabric.

| Dependency | Version |
| --- | --- |
| Fabric Loader | 0.19.3 or newer |
| e4mc | 6.2.3 (the regular build up to 1.21.11, the `modern` build from 26.1) |
| Java | 21 up to 1.21.11, 25 from 26.1 |

Minimum Fabric API and Simple Voice Chat versions for each Minecraft version:

| Minecraft | Fabric API | Simple Voice Chat |
| --- | --- | --- |
| 1.20.5 | 0.97.8+1.20.5 | 1.20.5-2.5.12 |
| 1.20.6 | 0.100.8+1.20.6 | 1.20.6-2.5.22 |
| 1.21 | 0.102.0+1.21 | 1.21.1-2.6.27 |
| 1.21.1 | 0.116.18+1.21.1 | 1.21.1-2.6.27 |
| 1.21.2 | 0.106.1+1.21.2 | 1.21.2-2.5.24 |
| 1.21.3 | 0.114.1+1.21.3 | 1.21.3-2.5.31 |
| 1.21.4 | 0.119.4+1.21.4 | 1.21.4-2.6.27 |
| 1.21.5 | 0.128.2+1.21.5 | 1.21.5-2.6.27 |
| 1.21.6 | 0.128.2+1.21.6 | 1.21.6-2.5.32 |
| 1.21.7 | 0.129.0+1.21.7 | 1.21.7-2.5.34 |
| 1.21.8 | 0.136.1+1.21.8 | 1.21.8-2.6.27 |
| 1.21.9 | 0.134.1+1.21.9 | 1.21.9-2.6.4 |
| 1.21.10 | 0.138.4+1.21.10 | 1.21.10-2.6.27 |
| 1.21.11 | 0.141.6+1.21.11 | 1.21.11-2.6.27 |
| 26.1 | 0.145.1+26.1 | 2.6.27 |
| 26.1.1 | 0.145.4+26.1.1 | 2.6.27 |
| 26.1.2 | 0.155.3+26.1.2 | 2.6.27 |
| 26.2 | 0.152.1+26.2 | 2.6.22 (2.6.x) |
| 26.3 | 0.166.0+26.3 | 2.6.27 |

## Good to know

- **Latency:** voice through e4mc may lag a bit more than direct voice chat on an unstable connection. The Minecraft connection resends lost packets instead of skipping them.
- **Bandwidth:** voice traffic goes through the e4mc relay along with the game.
- **Both sides need the mod.** A player with only Simple Voice Chat can't use voice through e4mc.
- Voice stays encrypted by Simple Voice Chat. This mod only forwards it.

## Troubleshooting

If voice chat doesn't connect, check `logs/latest.log`.

- **On the host:** look for `Tunneling voice chat of <player>`. If you see `joined through e4mc ... without e4mc Voice Compat` instead, that player is missing the mod.
- **On the joining player's side:** look for `Voice chat connection tunneled through e4mc`.

When reporting an issue, please include both logs: the host's and the joining player's.

## Links

- Source code and issues: [GitHub](https://github.com/HeetroNoft/e4mc-voice-compat)
- [e4mc](https://www.curseforge.com/minecraft/mc-mods/e4mc)
- [Simple Voice Chat](https://www.curseforge.com/minecraft/mc-mods/simple-voice-chat)

## License

MIT. This project isn't affiliated with e4mc or Simple Voice Chat.
