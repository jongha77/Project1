package com.example.project1

class User {
    var userName :String = ""
    constructor(username: String) {
        this.userName = username
    }
    constructor() {
       // this.userName = "DefaultUserName"
    }

    fun setUser(): String {
        return userName
    }
}