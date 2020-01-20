package com.mavenclinic.mobile.demo.common.di

import com.mavenclinic.mobile.demo.common.domain.repos.MetadataRepo
import com.mavenclinic.mobile.demo.common.domain.repos.production.MetadataRepoImpl
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ServiceLocator {

    val metadataRepo: MetadataRepo by lazy { MetadataRepoImpl() }
}