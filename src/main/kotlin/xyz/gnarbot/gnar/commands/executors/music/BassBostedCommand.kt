package xyz.gnarbot.gnar.commands.executors.music

import xyz.gnarbot.gnar.commands.*
import xyz.gnarbot.gnar.music.MusicManager
import xyz.gnarbot.gnar.music.TrackContext

@Command(
        aliases = ["bass"],
        description = "Boost the bass of the music"
)
@BotInfo(
        id = 85,
        category = Category.MUSIC,
        scope = Scope.VOICE,
        donor = true
)
class BassBoostedCommand : MusicCommandExecutor(true, true) {
    override fun execute(context: Context, label: String, args: Array<String>, manager: MusicManager) {
        if (args.isEmpty()) {
            context.send().embed {
                title { "Bass Boost" }
                field("Bass Boost Options", false) {
                    "`Off`, `Soft`, `Hard`, `Extreme`, and `EarRape`"
                }
            }.action().queue()
            return
        }

        val query = args[0].toLowerCase()

        if (manager.player.playingTrack.getUserData(TrackContext::class.java)?.requester == context.user.idLong) {

            when (query) {
                "off" -> manager.disableBass()
                "soft" -> manager.boostBass(0.25F, 0.15F)
                "hard" -> manager.boostBass(0.50F, 0.25F)
                "extreme" -> manager.boostBass(0.75F, 0.50F)
                "earrape" -> manager.boostBass(1F, 0.75F)

                else -> {
                    context.send().error("$query is not an option.").queue()
                    return
                }
            }

            context.send().embed {
                title { "Bass Boost" }
                field("Bass Boost", false) {
                    "Bass Boost has been set to: $query"
                }
            }.action().queue()
        } else {
            context.send().error("You did not request this song, so you can't change the bass.").queue()
        }
    }
}
