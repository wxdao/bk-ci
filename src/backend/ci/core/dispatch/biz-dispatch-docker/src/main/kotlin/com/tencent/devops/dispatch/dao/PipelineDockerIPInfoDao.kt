package com.tencent.devops.dispatch.dao

import com.tencent.devops.model.dispatch.tables.TDispatchPipelineDockerIpInfo
import com.tencent.devops.model.dispatch.tables.records.TDispatchPipelineDockerIpInfoRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Result
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PipelineDockerIPInfoDao {
    fun create(
        dslContext: DSLContext,
        idcIp: String,
        capacity: Int,
        used: Int,
        cpuLoad: Int,
        memLoad: Int,
        diskLoad: Int,
        diskIOLoad: Int,
        enable: Boolean,
        grayEnv: Boolean
    ) {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            dslContext.insertInto(
                this,
                DOCKER_IP,
                CAPACITY,
                USED_NUM,
                CPU_LOAD,
                MEM_LOAD,
                DISK_LOAD,
                DISK_IO_LOAD,
                ENABLE,
                GRAY_ENV,
                GMT_CREATE,
                GMT_MODIFIED
            ).values(
                idcIp,
                capacity,
                used,
                cpuLoad,
                memLoad,
                diskLoad,
                diskIOLoad,
                enable,
                grayEnv,
                LocalDateTime.now(),
                LocalDateTime.now()
            ).execute()
        }
    }

    fun update(
        dslContext: DSLContext,
        idcIp: String,
        capacity: Int,
        used: Int,
        cpuLoad: Int,
        memLoad: Int,
        diskLoad: Int,
        diskIOLoad: Int,
        enable: Boolean
    ) {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            dslContext.update(this)
                .set(CAPACITY, capacity)
                .set(USED_NUM, used)
                .set(CPU_LOAD, cpuLoad)
                .set(MEM_LOAD, memLoad)
                .set(DISK_LOAD, diskLoad)
                .set(DISK_IO_LOAD, diskIOLoad)
                .set(ENABLE, enable)
                .set(GMT_MODIFIED, LocalDateTime.now())
                .where(DOCKER_IP.eq(idcIp))
                .execute()
        }
    }

    fun updateDockerIpStatus(
        dslContext: DSLContext,
        id: Long,
        enable: Boolean
    ) {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            dslContext.update(this)
                .set(ENABLE, enable)
                .set(GMT_MODIFIED, LocalDateTime.now())
                .where(ID.eq(id))
                .execute()
        }
    }

    fun getDockerIpList(
        dslContext: DSLContext,
        page: Int,
        pageSize: Int
    ): Result<TDispatchPipelineDockerIpInfoRecord> {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            return dslContext.selectFrom(this)
                .limit(pageSize).offset((page - 1) * pageSize)
                .fetch()
        }
    }

    fun getDockerIpInfo(
        dslContext: DSLContext,
        dockerIp: String
    ): TDispatchPipelineDockerIpInfoRecord {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            return dslContext.selectFrom(this)
                .where(DOCKER_IP.eq(dockerIp))
                .fetchOne()
        }
    }

    fun getDockerIpCount(
        dslContext: DSLContext
    ): Long {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            return dslContext.selectCount()
                .from(this)
                .fetchOne(0, Long::class.java)
        }
    }

    fun getAvailableDockerIpList(
        dslContext: DSLContext,
        grayEnv: Boolean,
        cpuLoad: Int,
        memLoad: Int,
        diskLoad: Int,
        diskIOLoad: Int,
        limitIpSet: Set<String> = setOf()
    ): Result<TDispatchPipelineDockerIpInfoRecord> {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            val conditions =
                mutableListOf<Condition>(
                    ENABLE.eq(true),
                    GRAY_ENV.eq(grayEnv),
                    CPU_LOAD.lessOrEqual(cpuLoad),
                    MEM_LOAD.lessOrEqual(memLoad),
                    DISK_LOAD.lessOrEqual(diskLoad),
                    DISK_IO_LOAD.lessOrEqual(diskIOLoad)
                )
            if (limitIpSet.isNotEmpty()) conditions.add(DOCKER_IP.`in`(limitIpSet))

            return dslContext.selectFrom(this)
                .where(conditions)
                .orderBy(DISK_LOAD.asc())
                .fetch()
        }
    }

    fun getEnableDockerIpList(
        dslContext: DSLContext,
        grayEnv: Boolean
    ): Result<TDispatchPipelineDockerIpInfoRecord> {
        with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            return dslContext.selectFrom(this)
                .where(ENABLE.eq(true))
                .and(GRAY_ENV.eq(grayEnv))
                .fetch()
        }
    }

    fun delete(
        dslContext: DSLContext,
        ipInfoId: Long
    ): Int {
        return with(TDispatchPipelineDockerIpInfo.T_DISPATCH_PIPELINE_DOCKER_IP_INFO) {
            dslContext.delete(this)
                .where(ID.eq(ipInfoId))
                .execute()
        }
    }
}