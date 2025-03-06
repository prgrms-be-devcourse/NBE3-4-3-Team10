package com.ll.TeamProject.global.exceptions

import com.ll.TeamProject.global.rsData.RsData
import com.ll.TeamProject.standard.base.Empty

class ServiceException(val resultCode: String, val msg: String) : RuntimeException("$resultCode : $msg") {

    val rsData: RsData<Empty> by lazy { RsData(resultCode, msg) }
}
