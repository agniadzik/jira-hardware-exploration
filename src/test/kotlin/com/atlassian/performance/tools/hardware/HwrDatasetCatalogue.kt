package com.atlassian.performance.tools.hardware

import com.amazonaws.regions.Regions
import com.atlassian.performance.tools.aws.api.StorageLocation
import com.atlassian.performance.tools.awsinfrastructure.api.DatasetCatalogue
import com.atlassian.performance.tools.infrastructure.api.dataset.Dataset
import com.atlassian.performance.tools.infrastructure.api.dataset.HttpDatasetPackage
import com.atlassian.performance.tools.infrastructure.api.jira.JiraHomePackage
import com.atlassian.performance.tools.lib.LicenseOverridingDatabase
import com.atlassian.performance.tools.lib.infrastructure.AdminDataset
import com.atlassian.performance.tools.lib.infrastructure.ConfigurableMysqlDatabase
import com.atlassian.performance.tools.lib.overrideDatabase
import com.atlassian.performance.tools.lib.toExistingFile
import java.net.URI
import java.nio.file.Paths
import java.time.Duration

class HwrDatasetCatalogue {

    fun xl7Mysql() = Dataset(
        label = "7M issues JSW 7 MySQL",
        database = ConfigurableMysqlDatabase(
            source = HttpDatasetPackage(
                uri = URI("s3://jpt-custom-mysql-xl/dataset-7m-jira7/database.tar.bz2"),
                downloadTimeout = Duration.ofMinutes(55)
            ),
            extraDockerArgs = listOf(
                "--max_connections=151",
                "--innodb-buffer-pool-size=40G",
                "--innodb-log-file-size=2146435072"
            )
        ),
        jiraHomeSource = JiraHomePackage(
            HttpDatasetPackage(
                uri = URI("s3://jpt-custom-mysql-xl/dataset-7m-jira7/jirahome.tar.bz2"),
                downloadTimeout = Duration.ofMinutes(55))
        )
    ).overrideDatabase { original ->
        overrideLicense(original)
    }.let { dataset ->
        AdminDataset(
            dataset = dataset,
            adminLogin = "admin",
            adminPassword = "admin"
        )
    }

    fun xl8Mysql() = DatasetCatalogue().custom(
        location = StorageLocation(
            uri = URI("s3://jpt-custom-datasets-storage-a008820-datasetbucket-dah44h6l1l8p/")
                .resolve("dataset-6ed65a53-86cb-457e-a87f-cbcce67787c3"),
            region = Regions.EU_CENTRAL_1
        ),
        label = "7M issues JSW 8 MySQL",
        databaseDownload = Duration.ofMinutes(55),
        jiraHomeDownload = Duration.ofMinutes(55)
    ).overrideDatabase { original ->
        overrideLicense(original)
    }.let { dataset ->
        AdminDataset(
            dataset = dataset,
            adminLogin = "admin",
            adminPassword = "admin"
        )
    }

    fun l7Mysql() = DatasetCatalogue().custom(
        location = StorageLocation(
            uri = URI("s3://jpt-custom-datasets-storage-a008820-datasetbucket-1sjxdtrv5hdhj/")
                .resolve("a12fc4c5-3973-41f0-bf56-ede393677028"),
            region = Regions.EU_WEST_1
        ),
        label = "1M issues JSW 7 MySQL",
        databaseDownload = Duration.ofMinutes(20),
        jiraHomeDownload = Duration.ofMinutes(20)
    ).overrideDatabase { original ->
        overrideLicense(original)
    }.let { dataset ->
        AdminDataset(
            dataset = dataset,
            adminLogin = "admin",
            adminPassword = "admin"
        )
    }

    fun l8Mysql() = DatasetCatalogue().custom(
        location = StorageLocation(
            uri = URI("s3://jpt-custom-datasets-storage-a008820-datasetbucket-dah44h6l1l8p/")
                .resolve("dataset-2719279d-0b30-4050-8d98-0a9499ec36a0"),
            region = Regions.EU_CENTRAL_1
        ),
        label = "1M issues JSW 8 MySQL",
        databaseDownload = Duration.ofMinutes(20),
        jiraHomeDownload = Duration.ofMinutes(20)
    ).overrideDatabase { original ->
        overrideLicense(original)
    }.let { dataset ->
        AdminDataset(
            dataset = dataset,
            adminLogin = "admin",
            adminPassword = "admin"
        )
    }

    private fun overrideLicense(
        dataset: Dataset
    ): LicenseOverridingDatabase {
        val localLicense = Paths.get("jira-license.txt")
        return LicenseOverridingDatabase(
            dataset.database,
            listOf(
                localLicense
                    .toExistingFile()
                    ?.readText()
                    ?: throw Exception("Put a Jira license to ${localLicense.toAbsolutePath()}")
            ))
    }
}
