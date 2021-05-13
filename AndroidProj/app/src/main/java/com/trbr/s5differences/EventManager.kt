package om.trbr.s5differences


interface EMListener {
    fun callback(name: EventName, data: Any?): Boolean {
        return false
    }
}

const val profits_delay_limit = 10 * 60 * 1000L

enum class EventName {
    FIND_DIFFERENCES,
    FIND_DIFFERENCES_pre,
    FIND_ERR
}

object EventManager {
    private var registered: MutableMap<EventName, MutableList<EMListener>> = mutableMapOf()
    private var events: MutableMap<EventName, MutableList<Any?>> = mutableMapOf()
    private var events_t: MutableMap<EventName, MutableList<Any?>> = mutableMapOf()

    fun init() {
        Thread {
            while (true) {

                if (events.size > 0) {

                    synchronized(events) {
                        events_t = events.toMutableMap()
                        events.clear()
                    }

                    events_t.forEach { item ->
                        synchronized(registered) {
                            registered[item.key]?.forEach { em_callback ->
                                for (it in item.value) {
                                    if (em_callback.callback(item.key, it)) break
                                }
                            }
                        }
                    }
                }


                try {
                    Thread.sleep(100)
                } catch (ex: Exception) {
                }
            }
        }.start()
    }

    fun registerEvent(name: EventName, listener: EMListener) {
        synchronized(registered) {
            registered[name]?.add(listener)
                ?: registered.put(name, mutableListOf(listener))
        }
    }

    fun registerEvents(names: Array<EventName>, listener: EMListener) {
        synchronized(registered) {
            names.forEach { name ->
                registered[name]?.add(listener)
                    ?: registered.put(name, mutableListOf(listener))
            }
        }
    }

    fun un_registerEvent(listener: EMListener, name: EventName? = null) {
        synchronized(registered) {
            if (name != null) {
                registered[name]?.remove(listener)
            } else {
                registered.forEach { reg_item ->
                    registered[reg_item.key]?.removeAll { item_listener -> item_listener == listener }
                }
            }
        }
    }

    fun sendEvent(name: EventName, data: Any? = null) {
        synchronized(events) {
            events.get(name)?.add(data)
                ?: events.put(name, mutableListOf(data))
        }
    }
}
