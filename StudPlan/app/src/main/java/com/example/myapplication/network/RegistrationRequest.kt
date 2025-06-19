package com.example.myapplication.network

data class StudentRequest(
    val ticketNo: String,
    val firstName: String,
    val patronymic: String,
    val lastName: String,
    val city: String,
    val school: String,
    val college: String,
    val className: String,
    val groupName: String,
    val telephone: String,
    val createDept: Long = 101,
    val delFlag: String = "0",
    val remark: String? = null
)