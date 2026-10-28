# e4mc Voice Compat

![e4mc Voice Compat logo](src/main/resources/logo-128.png)

A Fabric mod for Minecraft 26.2 that makes [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) work for players who join a world through [e4mc](https://modrinth.com/mod/e4mc).

## The problem

e4mc opens your LAN world to the internet by tunneling the Minecraft connection through a relay (or peer-to-peer with Dialtone). Simple Voice Chat, however, sends audio over a **separate UDP port**, and e4mc doesn't forward that port. Players who join through e4mc can play normally, but their voice chat stays disconnected.

## What this mod does

For players connected through e4mc, voice packets travel **inside the Minecraft connection**, so they go through the e4mc tunnel like the rest of the game.

- Players connected through the **e4mc relay** (`*.e4mc.link` address) or **Dialtone** are tunneled.
- The **host** and players on the **direct LAN** keep using regular UDP. Nothing changes for them.
- A player who joins through e4mc **without** this mod can still play, and gets a chat message explaining why voice chat doesn't work for them.

## Requirements

| Dependency | Version |
| --- | --- |
| Minecraft | 26.2 |
| Fabric Loader | 0.19.3 or newer |
| Fabric API | any version supported by Simple Voice Chat (0.152.1+26.2 or newer) |
| e4mc | 6.2.3 (`modern` build) |
| Simple Voice Chat | 2.6.22 or newer (2.6.x) |
| Java | 25 |

## Installation

Put the mod in the `mods` folder of **the host and every player**, alongside e4mc, Simple Voice Chat and Fabric API.

Nothing to configure: open your world to LAN as usual, share the e4mc address, and voice chat connects on its own.

## How it works

1. When a player joins, the host checks how they arrived: e4mc hands the server a QUIC stream for relay connections and a Dialtone channel for peer-to-peer connections, while direct connections use a regular socket.
2. If the player came through e4mc and has the mod, the host tells their client to tunnel voice. The client receives this before Simple Voice Chat's own connection details.
3. On the host, the mod provides Simple Voice Chat with a socket that serves both cases: UDP for direct players, and a virtual address per tunneled player whose packets are sent through their Minecraft connection.
4. On the client, Simple Voice Chat uses a socket that sends and receives through the Minecraft connection instead of UDP.

Voice packets stay encrypted by Simple Voice Chat. The mod only forwards them, and handles them on the network thread so they never wait for a server tick.

Everything goes through Simple Voice Chat's public plugin API, plus three small mixins:

- intercepting voice packets on the network thread, on the server and on the client;
- letting Simple Voice Chat accept a Dialtone address, which it otherwise rejects because it isn't an IP address.

## Known limitations

- **Latency through the tunnel.** Unlike UDP, the Minecraft connection is reliable and ordered: a lost packet delays the ones behind it instead of being skipped. On an unstable connection, tunneled voice may lag more than direct UDP voice would.
- **Relay bandwidth.** Voice traffic adds to the traffic going through the e4mc relay.
- **Both sides need the mod.** A vanilla Simple Voice Chat client can't use the tunnel.

## Troubleshooting

If voice chat doesn't connect, look in `logs/latest.log`:

### On the host

- `Using custom voicechat socket implementation: io.github.heet.e4mcvoice...` means the mod is active.
- `Voice chat server started at port ...` means Simple Voice Chat's server is running.
- `Tunneling voice chat of <player> through the Minecraft connection (e4mc relay|Dialtone)` means the player was detected and tunneled.
- `<player> joined through e4mc (...) without e4mc Voice Compat` means that player is missing the mod.

### On the joining player's side

- `Received secret` means Simple Voice Chat received its connection details.
- `Voice chat connection tunneled through e4mc` means the tunnel is in use.

When reporting an issue, please attach both logs: the host's and the joining player's.

## Building from source

The build uses local copies of the e4mc and Simple Voice Chat jars. They aren't committed, so put them in `libs/`:

```text
libs/e4mc-fabric-6.2.3-modern.jar
libs/voicechat-fabric-2.6.22+26.2.jar
```

Then:

```sh
./gradlew build       # builds build/libs/e4mc_voice_compat-<version>.jar and runs the tests
./gradlew runClient   # starts a dev client with the jars from libs/ in run/mods
```

The build needs a Java 25 JDK. Gradle picks it up through the toolchain set in `gradle/gradle-daemon-jvm.properties`.

## License

[MIT](LICENSE). e4mc and Simple Voice Chat are the work of their respective authors, and this project isn't affiliated with either of them.
