package at.ac.tuwien.fundify.bootstrap.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DependencyRuleTest {

    private static final String ROOT_PACKAGE = "at.ac.tuwien.fundify";
    private static final String DOMAIN_PACKAGE = "domain";
    private static final String FUNDING_PACKAGE = "domain.funding";
    private static final String ANNOTATING_PACKAGE = "domain.annotating";
    private static final String APPLICATION_PACKAGE = "application";
    private static final String PORT_PACKAGE = "application.port";
    private static final String SERVICE_PACKAGE = "application.service";
    private static final String ADAPTERS_PACKAGE = "adapters";
    private static final String BOOTSTRAP_PACKAGE = "bootstrap";
    private static final String IN_ADAPTERS_PACKAGE = "adapters.in";
    private static final String OUT_ADAPTERS_PACKAGE = "adapters.out";

    @Test
    void checkDependencyRule() {
        String importPackages = ROOT_PACKAGE + "..";
        JavaClasses classesToCheck = new ClassFileImporter().importPackages(importPackages);

        // the domain layer only depends on itself
        checkNoDependencyFromTo(DOMAIN_PACKAGE, APPLICATION_PACKAGE, classesToCheck);
        checkNoDependencyFromTo(DOMAIN_PACKAGE, ADAPTERS_PACKAGE, classesToCheck);
        checkNoDependencyFromTo(DOMAIN_PACKAGE, BOOTSTRAP_PACKAGE, classesToCheck);

        // the subdomains only depend on the common subdomain
        checkNoDependencyFromTo(FUNDING_PACKAGE, ANNOTATING_PACKAGE, classesToCheck);
        checkNoDependencyFromTo(ANNOTATING_PACKAGE, FUNDING_PACKAGE, classesToCheck);

        // the application layer only depends on the domain layer
        checkNoDependencyFromTo(APPLICATION_PACKAGE, ADAPTERS_PACKAGE, classesToCheck);
        checkNoDependencyFromTo(APPLICATION_PACKAGE, BOOTSTRAP_PACKAGE, classesToCheck);

        // the port package and the service package do not depend on each other
        checkNoDependencyFromTo(PORT_PACKAGE, SERVICE_PACKAGE, classesToCheck);

        // the adapters do not use application services directly, but use ports
        checkNoDependencyFromTo(ADAPTERS_PACKAGE, SERVICE_PACKAGE, classesToCheck);

        // the adapters layer only depends on the application layer and domain layer
        checkNoDependencyFromTo(ADAPTERS_PACKAGE, BOOTSTRAP_PACKAGE, classesToCheck);

        // in- and out- adapters do not depend on each other
        checkNoDependencyFromTo(IN_ADAPTERS_PACKAGE, OUT_ADAPTERS_PACKAGE, classesToCheck);

    }

    private void checkNoDependencyFromTo(
            String fromPackage, String toPackage, JavaClasses classesToCheck) {
        noClasses()
                .that()
                .resideInAPackage(fullyQualified(fromPackage))
                .should()
                .dependOnClassesThat()
                .resideInAPackage(fullyQualified(toPackage))
                .check(classesToCheck);
    }

    private String fullyQualified(String packageName) {
        return ROOT_PACKAGE + '.' + packageName + "..";
    }
}