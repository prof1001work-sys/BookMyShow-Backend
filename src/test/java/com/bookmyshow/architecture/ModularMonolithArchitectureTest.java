package com.bookmyshow.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

/**
 * ArchUnit architecture governance tests enforcing Modular Monolith boundary rules,
 * clean architecture layering, and production coding standards.
 */
@AnalyzeClasses(packages = "com.bookmyshow", importOptions = ImportOption.DoNotIncludeTests.class)
public class ModularMonolithArchitectureTest {

    // =========================================================================
    // 1. Coding Standards Rules
    // =========================================================================

    @ArchTest
    static final ArchRule no_field_injection = fields()
            .should().notBeAnnotatedWith(Autowired.class)
            .because("Field injection is forbidden. Always use constructor injection.");

    @ArchTest
    static final ArchRule no_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
            .because("Standard System.out and System.err logging is forbidden. Use SLF4J logger.");

    @ArchTest
    static final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
            .because("Use SLF4J with Logback instead of java.util.logging.");

    // =========================================================================
    // 2. Layering & Component Rules
    // =========================================================================

    @ArchTest
    static final ArchRule controllers_must_reside_in_api_layer = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..api..")
            .allowEmptyShould(true)
            .because("REST controllers must strictly reside inside the 'api' layer of a module.");

    // =========================================================================
    // 3. Modular Monolith Isolation Rules
    // =========================================================================

    @ArchTest
    static final ArchRule booking_should_not_depend_on_theatre_infrastructure = noClasses()
            .that().resideInAPackage("com.bookmyshow.booking..")
            .should().dependOnClassesThat().resideInAPackage("com.bookmyshow.theatre.infrastructure..")
            .because("Booking module must never access Theatre infrastructure directly.");

    @ArchTest
    static final ArchRule payment_should_not_depend_on_booking_infrastructure = noClasses()
            .that().resideInAPackage("com.bookmyshow.payment..")
            .should().dependOnClassesThat().resideInAPackage("com.bookmyshow.booking.infrastructure..")
            .because("Payment module must never access Booking infrastructure directly.");

    @ArchTest
    static final ArchRule movie_should_not_depend_on_booking_infrastructure = noClasses()
            .that().resideInAPackage("com.bookmyshow.movie..")
            .should().dependOnClassesThat().resideInAPackage("com.bookmyshow.booking.infrastructure..")
            .because("Movie module must never access Booking infrastructure directly.");

    @ArchTest
    static final ArchRule notification_should_not_depend_on_booking_infrastructure = noClasses()
            .that().resideInAPackage("com.bookmyshow.notification..")
            .should().dependOnClassesThat().resideInAPackage("com.bookmyshow.booking.infrastructure..")
            .because("Notification module must never access Booking infrastructure directly.");

    @ArchTest
    static final ArchRule modules_should_not_depend_on_other_module_internal_infrastructure = noClasses()
            .that().resideInAPackage("com.bookmyshow.iam..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.bookmyshow.booking.infrastructure..",
                    "com.bookmyshow.payment.infrastructure..",
                    "com.bookmyshow.movie.infrastructure..",
                    "com.bookmyshow.theatre.infrastructure.."
            )
            .because("IAM module must not access other module infrastructure directly.");
}
