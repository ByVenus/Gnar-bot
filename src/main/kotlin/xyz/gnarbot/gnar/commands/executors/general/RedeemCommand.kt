package xyz.gnarbot.gnar.commands.executors.general

import xyz.gnarbot.gnar.commands.BotInfo
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.CommandExecutor
import xyz.gnarbot.gnar.commands.Context
import xyz.gnarbot.gnar.db.PremiumKey
import xyz.gnarbot.gnar.db.Redeemer
import xyz.gnarbot.gnar.db.guilds.UserData

import java.awt.*
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.StringJoiner

@Command(aliases = arrayOf("redeem"), usage = "(code)", description = "Redeem a key for your server.")
@BotInfo(id = 21)
class RedeemCommand : CommandExecutor() {
    override fun execute(context: Context, label: String, args: Array<String>) {
        if (args.size == 0) {
            context.bot.commandDispatcher.sendHelp(context, info)
            return
        }

        val id = args[0]

        val key = context.bot.db().getPremiumKey(id)

        if (key != null) {
            if (key.redeemer != null) {
                context.send().error("That code has already been redeemed.").queue()
                return
            }

            when (key.type) {
                PremiumKey.Type.PREMIUM -> {
                    key.setRedeemer(Redeemer(Redeemer.Type.GUILD, context.guild.id)).save()
                    println(key.redeemer)

                    context.data.addPremiumKey(key.id, key.duration).save()

                    val expiresBy = OffsetDateTime.ofInstant(Instant.ofEpochMilli(context.data.premiumUntil), ZoneId.systemDefault())

                    context.send().embed("Premium Code")
                            .setColor(Color.ORANGE)
                            .description("Redeemed key `" + key.id + "`. **Thank you for supporting the bot's development!**\n")
                            .appendDescription("Your **Premium** status will be valid until `" + expiresBy.format(DateTimeFormatter.RFC_1123_DATE_TIME) + "`.")
                            .field("**Key Type:** ${key.type.description}", true, StringJoiner("\n"))
                            .field("Donator Perks", true, StringJoiner("\n")
                                    .add("• `volume` Change the volume of the music player!")
                                    .add("• First access to new features.")
                                    .add("• Use the music bot during maximum music capacity.")
                                    .add("• Bass Boosted Music!")
                                    .add("• Access to Jinx (Gnar Alpha bot)")
                            )
                            .action().queue()
                }
                PremiumKey.Type.PREMIUM_OVERRIDE -> {
                    key.setRedeemer(Redeemer(Redeemer.Type.PREMIUM_OVERRIDE, context.user.id)).save()
                    println(key.redeemer)

                    context.bot.options.ofUser(context.user).addPremiumKey(key.id, key.duration).save()

                    val expiration = OffsetDateTime.ofInstant(Instant.ofEpochMilli(context.data.premiumUntil), ZoneId.systemDefault())

                    context.send().embed("Premium Code")
                            .setColor(Color.ORANGE)
                            .description("Redeemed key `" + key.id + "`. **Thank you for supporting the bot's development!**\n")
                            .appendDescription("Your **Premium** status will be valid until `" + expiration.format(DateTimeFormatter.RFC_1123_DATE_TIME) + "`.")
                            .field("**Key Type:** ${key.type.description}", true, StringJoiner("\n"))
                            .field("Donator Perks", true, StringJoiner("\n")
                                    .add("• `volume` Change the volume of the music player!")
                                    .add("• First access to new features.")
                                    .add("• Use the music bot during maximum music capacity.")
                                    .add("• Bass Boosted Music!")
                                    .add("• Access to Jinx (Gnar Alpha bot)")
                            )
                            .action().queue()
                }
                else -> context.send().error("Unknown key type.").queue()
            }
        } else {
            context.send().error("That is not a valid code.").queue()
        }
    }
}