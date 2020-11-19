package x.samantha.commandsjda.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Command(val name: String, vararg val aliases: String)
