package org.eu.smileyik.numericalrequirements.core.api.player;

/**
 * 继承此接口后，该值将会在注册给玩家时瞬间运行并且不保存到玩家数据当中，即运行完后瞬间销毁。
 * 在注册给玩家时，会立即执行PlayerKey中的handlePlayer方法。
 */
public interface PlayerValueOneShot {
}
