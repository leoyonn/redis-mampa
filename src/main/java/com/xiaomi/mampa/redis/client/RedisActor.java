/**
 * Actor.java
 * [CopyRight]
 * @author leo [liuy@xiaomi.com]
 * @date Mar 31, 2014 5:02:07 PM
 */
package com.xiaomi.mampa.redis.client;

import io.netty.channel.Channel;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaomi.mampa.actor.ActorGroup;
import com.xiaomi.mampa.actor.IActor;
import com.xiaomi.mampa.actor.NextState;
import com.xiaomi.mampa.actor.router.DefaultFsmRouter;
import com.xiaomi.mampa.actor.router.OneActorRouter;
import com.xiaomi.mampa.event.IAction;
import com.xiaomi.mampa.event.IEventType;
import com.xiaomi.mampa.fsm.AbstractRuleSet;
import com.xiaomi.mampa.fsm.IFsmType;
import com.xiaomi.mampa.fsm.IFsmType.DefaultFsmType;
import com.xiaomi.mampa.fsm.IStateType;
import com.xiaomi.mampa.fsm.State;
import com.xiaomi.mampa.redis.command.Command;
import com.xiaomi.mampa.redis.reply.Reply;

import static com.xiaomi.mampa.redis.client.RedisActor.EventType.Recv;
import static com.xiaomi.mampa.redis.client.RedisActor.EventType.Send;
import static com.xiaomi.mampa.redis.client.RedisActor.EventType.Timeout;
import static com.xiaomi.mampa.redis.client.RedisActor.StateType.Idle;
import static com.xiaomi.mampa.redis.client.RedisActor.StateType.Stop;

/**
 * Actor for queueing redis-request-command, send request to redis-server, and deliver result from server.
 * 
 * @author leo
 */
public class RedisActor {
    private static final Logger logger = LoggerFactory.getLogger(RedisActor.class);
    private static final String ONE = "1";

    /**
     * State Type of Client FSM.
     * 
     * @author leo
     */
    public static enum StateType implements IStateType {
        Idle, Stop;
    }

    /**
     * Event Type sent to Client FSM.
     * 
     * @author leo
     */
    public static enum EventType implements IEventType {
        Send, Recv, Timeout;
    }

    public class StateValue {
        private LinkedList<Command<?, ?, ?>> queue = new LinkedList<Command<?, ?, ?>>();

        void send(Command<?, ?, ?> command) {
            queue.add(command);
        }

        void recv(Reply.Raw raw) {
            Command<?, ?, ?> command = queue.remove();
            command.returns(raw);
        }

    }

    private ActorGroup<String, String> actor;
    private Channel channel;

    protected class RuleSet extends AbstractRuleSet<String, StateValue> {
        public RuleSet() {
            super(DefaultFsmType.One, StateType.values().length, EventType.values().length, Timeout, Stop);
        }

        @Override
        public boolean init(State<StateValue> state, String fsmTarget, IEventType etype, Object data,
                IActor<String> master) {
            state.type(Idle).value(new StateValue());
            logger.info("Init done!");
            return true;
        }

        @Override
        protected void buildRules() {
            addRule(Send, new IAction<String, StateValue>() {
                @Override
                public NextState exec(State<StateValue> state, IFsmType fsmType, String fsmTarget, IEventType etype,
                        Object data, IActor<String> master) throws Exception {
                    logger.debug("Sending: {} {}", etype, data);
                    Command<?, ?, ?> command = (Command<?, ?, ?>) data;
                    state.value().send(command);
                    channel.writeAndFlush(command);
                    return nextState(master, Idle);
                }
            });
            addRule(Recv, new IAction<String, StateValue>() {
                @Override
                public NextState exec(State<StateValue> state, IFsmType fsmType, String fsmTarget, IEventType etype,
                        Object data, IActor<String> master) throws Exception {
                    logger.debug("Receiving: {} {}", etype, data);
                    state.value().recv((Reply.Raw) data);
                    return nextState(master, Idle);
                }
            });
        }

        @Override
        protected IAction<String, StateValue> buildDefaultRule() {
            return new IAction<String, StateValue>() {
                @Override
                public NextState exec(State<StateValue> state, IFsmType fsmType, String fsmTarget, IEventType etype,
                        Object data, IActor<String> master) throws Exception {
                    logger.warn("Invalid request: {} {}", etype, data);
                    return nextState(master, Idle);
                }
            };
        }

    }

    @SuppressWarnings("unchecked")
    protected RedisActor() {
        actor = new ActorGroup<String, String>(1, OneActorRouter.instance(), DefaultFsmRouter.stringInstance(), 1, 1,
                new RuleSet());
    }

    public void start(Channel channel) {
        this.channel = channel;
        actor.start();
    }

    public boolean send(Command<?, ?, ?> command) {
        return actor.tell(ONE, DefaultFsmType.One, ONE, Send, command);
    }

    public boolean recv(Reply.Raw raw) {
        return actor.tellSync(ONE, DefaultFsmType.One, ONE, Recv, raw);
    }

    public void shutdown() {
        actor.shutdown();
        channel = null;
    }
}
