/*
 * Copyright 2019-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package net.mamoe.mirai.mock.contact

import net.mamoe.kjbb.JvmBlockingBridge
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.mock.MockBot
import net.mamoe.mirai.mock.MockBotDSL

@JvmBlockingBridge
@Suppress("unused")
public interface MockContact : Contact {
    override val bot: MockBot


    /**
     * 令 [MockContact] 发出一条信息, 并广播相关的消息事件 (如 [GroupMessageEvent])
     *
     * @return 返回 [MockContact] 发出的消息 (包含 [MessageSource]),
     *         可用于测试消息发出后马上撤回 `says().recall()`
     */
    @MockBotDSL
    public suspend infix fun says(message: MessageChain): MessageChain


    @MockBotDSL
    public suspend infix fun says(message: Message): MessageChain {
        return says(message.toMessageChain())
    }

    @MockBotDSL
    public suspend infix fun says(message: String): MessageChain {
        return says(PlainText(message))
    }
}
