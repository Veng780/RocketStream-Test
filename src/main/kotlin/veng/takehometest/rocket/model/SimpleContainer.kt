package veng.takehometest.rocket.model

class SimpleContainer<T> (private val id: Int, private val property: String, private val data: Collection<T>)
    : HashMap<String, Collection<T>>(1) {

    init {
        put(property, data)
    }
}