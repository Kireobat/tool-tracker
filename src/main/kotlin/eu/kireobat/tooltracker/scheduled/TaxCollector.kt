package eu.kireobat.tooltracker.scheduled

import eu.kireobat.tooltracker.api.dto.inbound.CreateFeeDto
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_LATE_FEE_AMOUNT_NOK
import eu.kireobat.tooltracker.common.Constants.Companion.DEFAULT_LATE_FEE_REASON
import eu.kireobat.tooltracker.service.FeeService
import eu.kireobat.tooltracker.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@Component
class TaxCollector(
    private val feeService: FeeService,
    private val userService: UserService,
) {

    private val logger = LoggerFactory.getLogger(TaxCollector::class.java)

    @Scheduled(cron = "0 0 */1 * * *") // At 0 minutes past the hour, every hour
    fun checkForBrokenContracts() {
        val taxCollectorUser = userService.findById(4).orElseThrow { throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The tax collector user could not be found") }

        logger.info("Checking for broken contracts... ")

        val brokenContracts = feeService.findOffendingLendingAgreements().filter { agreement ->
        val taxCollectorFees = feeService.findFeesByLendingAgreementId(agreement.id)
            .filter { fee -> fee.createdBy == taxCollectorUser }
        
        // Create fee if: no tax collector fees exist OR the most recent tax collector fee is older than 7 days
        taxCollectorFees.isEmpty() || 
        taxCollectorFees.maxByOrNull { fee -> fee.createdTime }!!.createdTime <= ZonedDateTime.now().minusDays(7)
    }

        logger.info("Found ${brokenContracts.size} broken contracts.")

        brokenContracts.forEach { contract ->
            feeService.createFee(
                CreateFeeDto(contract.id, DEFAULT_LATE_FEE_REASON, DEFAULT_LATE_FEE_AMOUNT_NOK),
                taxCollectorUser
            )
        }

        logger.info("Created late fees for ${brokenContracts.size} broken contracts.")
    }
}