package org.eu.smileyik.numericalrequirements.core.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指令注解， 使用例子：
 * 实现以下指令代码为
 * <pre>
 *     cmd sub1 abc
 *     cmd sub2 abc [player]
 *     cmd sub2 abc [player] [number]
 * </pre>
 * <pre><code>
 *     &#064;Command(value = "cmd", aliases = "a")
 *     public static class RootCommand {
 *
 *     }
 *
 *     &#064;Command(value = "sub1", parentCommand = "cmd")
 *     public static class SubCommand1 {
 *         &#064;Command("abc")
 *         public void abc(CommandSender sender, String[] args) {
 *             sender.sendMessage(Arrays.toString(args));
 *         }
 *     }
 *
 *     &#064;Command(value = "sub2", parentCommand = "cmd")
 *     public static class SubCommand2 {
 *         &#064;Command(value = "abc", args = {"player", "number"})
 *         public void abc(CommandSender sender, String[] args) {
 *             sender.sendMessage(Arrays.toString(args));
 *         }
 *
 *         &#064;Command(value  = "abc", args = {"player"})
 *         public void abc2(CommandSender sender, String[] args) {
 *             sender.sendMessage(Arrays.toString(args));
 *         }
 *     }
 * </code></pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * 指令.
     * @return
     */
    String value();
    String colorCode() default "&b";

    /**
     * 指令别名.
     * @return
     */
    String[] aliases() default {};

    /**
     * 指令权限，如果该注解是放置在类上，
     * 则会覆盖该类下的带有此注解且没有设置权限的的方法的权限设置.
     * @return
     */
    String permission() default "";

    /**
     * 父指令，仅当此注解标记在类上才有用。
     * @return
     */
    String parentCommand() default "";

    /**
     * 是否需要玩家来执行，当且仅当此注解标记在方法时生效。
     * @return
     */
    boolean needPlayer() default false;

    /**
     * 指令描述，用来描述指令用途。
     * @return
     */
    String description() default "description";

    /**
     * 设置参数，此参数仅为**变量**，相当于一个占位符。仅当注解放置在方法上生效。
     * @return
     */
    String[] args() default {};

    /**
     * 是否对变量长度不限制，即无视给定args()的长度。
     * @return
     */
    boolean isUnlimitedArgs() default false;
}
