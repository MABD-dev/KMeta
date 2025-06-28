package examples.mimic_data_class

import mimic_data_class.Copy


@Copy
class User(
    val age: Int,
    val name: String,
)

fun main() {
    val user = User(1, "someone")
    val user2 = user.copy(age = 10)

    println(user.toNiceString())
    println(user2.toNiceString())
}

// TODO: create processor to generate this function on non data-classes
private fun User.toNiceString(): String {
    return "User(age=${this.age}, name=${this.name})"
}