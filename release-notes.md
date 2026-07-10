# Release Notes

## v0.2.10

# Release Notes: Version 0.2.10

## Other Changes
- **Chore:** Merged updates from `main` into the release branch. (various commits) 

No new features or bug fixes were introduced in this release.

--- 

## v0.2.11

# Release Notes - Version 0.2.11

## Other Changes
- **Dependencies**: Bumped `microsphere-spring-boot` to version 0.2.12.  
- **Docs**: Updated branch latest versions in `README`.  
- **CI/CD**:  
  - Removed `spring-cloud-2025.1` from CI matrix.  
  - Re-added `spring-cloud-2025.1` to CI matrix.  
- **Maintenance**: Fixed indentation issue in Dependabot configuration.  

## v0.2.12

# Release Notes for v0.2.12

## Dependency Updates
- Bumped `org.junit:junit-bom` from 6.0.3 to 6.1.0. (#121)
- Bumped `microsphere-spring-boot` to 0.2.13.
- Updated project parent version to 0.2.9.

## Documentation
- Refined README with updated branch names.
- Updated README branch versions to reflect `0.2.12` / `0.1.12`.
- Added complete changelog and refined release notes.

## Build and Workflow Enhancements
- Updated Maven CI workflows and fixed EOF issues.
- Added Maven server credentials to CI pipelines.

## Other Changes
- Merged multiple updates from the main branch into the release branch. [skip ci]

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.11...0.2.12## v0.2.13

# Release Notes - Version 0.2.13

## Documentation
- Fixed formatting issues and removed `zread` badge in `README.md`.  
- Corrected typo for the `@EnableFeignClients` annotation in `README.md`.  
- Rewrote `README.md` with comprehensive sections for better clarity.  

## Dependency Updates
- Bumped `spring-boot` version for compatibility and enhancements.

## Build and Workflow Enhancements
- Added GitHub prompts to streamline contribution and collaboration.

---

*Thank you for using this version!*

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.12...0.2.13## v0.2.14

# Release Notes for v0.2.14

## Dependency Updates
- Upgraded `microsphere-spring-boot` to version 0.2.16.  
- Updated parent POM to version 0.3.0.

## Bug Fixes
- Fixed README branch version inconsistencies.  
- Updated README versions for clarity and corrected formatting.

## Build and Workflow Enhancements
- Removed redundant JavaDoc lines and normalized formatting/whitespace in Java files.  
- Eliminated trailing newlines, duplicated line separators, and trailing whitespace across Java source code.

## Other Changes
- Improved code quality by replacing raw collection instantiations with utility methods.  
- Merged multiple updates from the `main` branch.  

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.13...0.2.14## v0.2.15

# Release Notes for Version 0.2.15

## Dependency Updates
- Updated `microsphere-spring-boot` to version `0.2.17`. ([a131d10](https://github.com/your-repo/commit/a131d10))

## Documentation
- Updated README with the latest version information. ([f2507ea](https://github.com/your-repo/commit/f2507ea))

## Build and Workflow Enhancements
- Merged `main` into `release` to keep branches up-to-date. ([0690aad](https://github.com/your-repo/commit/0690aad), [5fa78e0](https://github.com/your-repo/commit/5fa78e0))
- Incremented version to prepare for the next patch release after `0.2.14`. ([9cceec3](https://github.com/your-repo/commit/9cceec3))
- Merged `release` branch changes back into `main`. ([393314e](https://github.com/your-repo/commit/393314e))

---

**Note:** This release primarily includes dependency updates, minor documentation edits, and maintenance tasks.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.14...0.2.15## v0.2.16

# Release Notes for Version 0.2.16

## Dependency Updates
- Upgraded `microsphere-spring-boot` to version **0.2.18**.
- Updated parent POM version to **0.3.1**.

## Documentation
- Updated README to reflect branch versions **0.2.16/0.1.16**.

## Other Changes
- Version bump to **0.2.16** post-publishing of **0.2.15**.
- Merged `main` into `release` to streamline branches.

---

Thank you for using our project! 🚀

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.15...0.2.16## v0.2.17

# Release Notes for Version 0.2.17

## New Features
- **Auto-Registration**: Automatically register `HasFeatures` from properties.  
- **Configurations**: Add conditional and switch `HasFeatures` bean names.  

## Other Changes
- **Service Changes**: Disable service registration endpoints.  

---

**Note**: For a detailed list of changes, please refer to the full changelog.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.16...0.2.17## v0.2.18

# Release Notes - Version 0.2.18

## New Features
- **Auto-Service Registration**: Added conditional configuration for auto-service registration. ([#3341a57](https://github.com/your-repo/commit/3341a57))
- **Discovery Clients**: Introduced availability conditions for discovery clients. ([#f85332b](https://github.com/your-repo/commit/f85332b))
- **Web Autoconfiguration**: Reference web autoconfiguration by name in `AutoConfigureAfter`. ([#bac8e6c](https://github.com/your-repo/commit/bac8e6c))

## Bug Fixes
- Removed unused imports in discovery and feature classes for cleaner code. ([#4867531](https://github.com/your-repo/commit/4867531))

## Dependency Updates
- Bumped `microsphere-spring-boot` to version 0.2.21. ([#d96feac](https://github.com/your-repo/commit/d96feac))
- Updated parent version to 0.3.4. ([#475e365](https://github.com/your-repo/commit/475e365))
- Bumped `microsphere-spring-boot` to version 0.2.20. ([#0bf2437](https://github.com/your-repo/commit/0bf2437))

## Documentation
- Updated `README` references to versions 0.2.18 and 0.1.18. ([#bf5673b](https://github.com/your-repo/commit/bf5673b))

## Build and Workflow Enhancements
- Regular merges between `main` and `release` branches to keep branches in sync. ([Multiple commits tagged `[skip ci]`](https://github.com/your-repo/commits))

---

*Thank you for using and supporting our project!* 🎉

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.17...0.2.18## v0.2.19

# Release Notes - Version 0.2.19

## Dependency Updates
- Updated `microsphere-spring-boot` to version `0.2.22`. ([d08f684](#))

## Documentation
- Updated README to reflect the latest version information. ([8faf95f](#))

## Build and Workflow Enhancements
- Merged `main` into `release` and `release` into `main`. ([44aa456](#), [5747cab](#))
- Bumped version to next patch after publishing `0.2.18`. ([eb566d9](#))

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.18...0.2.19## v0.2.20

# Release Notes: Version 0.2.20

## New Features
- Added support for Spring Cloud commons feature mappings. 
- Introduced `FeaturesConstants` for defining Spring Cloud features.
- Created `FeaturesUtils` for handling feature-related properties and names.
- Implemented `FeaturesProperties` configuration class for registering feature-related beans.
- Added default `features.yaml` file for Spring Cloud features.

## Bug Fixes
- Fixed `PROPERTY_NAME_PREFIX` value for feature registration.  
- Returned empty maps from feature getters to handle edge cases.

## Dependency Updates
- Bumped `microsphere-spring-boot` to version 0.2.27 (includes 0.2.23, 0.2.24, and 0.2.25 updates).
- Upgraded `spring-cloud-dependencies` to version 2025.1.2.  
- Bumped parent POM to `microsphere-build` version 0.3.5.  
- Updated BOM and JUnit versions.

## Documentation
- Updated README with latest branch versions.

## Test Improvements
- Added unit tests for `FeaturesUtils` and `FeaturesConstants`.
- Introduced additional assertions for the `FeaturesEndpoint`.

## Build and Workflow Enhancements
- Removed unused imports across multiple classes, improving code clarity.
- Optimized module features mapping by making `moduleFeaturesMap` local and utilizing `BeanFactory`.

## Other Changes
- Minor reorganization and clarification for feature property keys in `defaults.yaml`.  
- Used colon separator for qualified feature names in tests and setup.

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.19...0.2.20## v0.2.21

# Release Notes - Version 0.2.21

### New Features
- **OpenFeign:** Added feature class metadata for enhanced functionality. (#e077a59)

### Documentation
- Updated README with the latest release version information. (#a5c680e)

### Dependency Updates
- Upgraded `microsphere-spring-boot` to the latest version. (#136cc5f)

### Build and Workflow Enhancements
- Merged main into release and release into main for alignment. [skip ci] (#5139232, #09cd034)
- Bumped version to the next patch post 0.2.20 release. (#d66fae5)

---

**Note:** For additional details, refer to the full commit history.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.20...0.2.21## v0.2.22

# Release Notes for Version 0.2.22

## New Features
- **Feign Auto-Refresh**: Added auto-configuration for Feign auto-refresh components.  
- **OpenFeign Enhancements**:  
  - Introduced `@ConditionalOnOpenFeignEnabled` annotation.  
  - Added OpenFeign availability condition annotation and constants.  
  - Refactored OpenFeign condition constants usage.

## Bug Fixes
- Fixed integration test class name typo in `web registry` tests.

## Documentation
- **Javadocs**:  
  - Clarified Javadoc example headings and cleaned up formatting.  
  - Updated `@since` tags for OpenFeign to `1.0.0`.  
  - Refined class Javadocs for integration tests.  
- **README**: Updated version matrix to reflect the latest version.  

## Dependency Updates
- Bumped `microsphere-spring-boot` to version `0.2.30`.  
- Upgraded parent POM to `microsphere-build` version `0.3.8`.  

## Test Improvements
- Added integration tests for service endpoints, auto-registration, and Tomcat fault tolerance.  
- Simplified setup for discovery auto-config tests.  
- Refined integration tests for registry and auto-registration functionality.  

## Build and Workflow Enhancements
- Centralized application name placeholder with `@Value`.  
- Improved handling of merge conflicts for continuous integration (`chore: merge main into release`).  

## Other Changes
- Refined imports, annotations, and formatting across configurations and tests.  
- Removed redundant encoding property from configurations.  
- Introduced class constants to streamline conditions in auto-configuration.  

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.21...0.2.22## v0.2.23

# Release Notes for Version 0.2.23

## New Features
- **Spring Boot Processors**: Added Spring Boot processors to module POMs for enhanced module configuration. (#1d24512)
- **WebFlux**: Introduced auto-configuration test for WebFlux service registry. (#4e33b01)

## Bug Fixes
- **Service Registry**: Fixed auto-configuration ordering issue in service registry. (#5206d8b)

## Dependency Updates
- **Microsphere**: Upgraded `microsphere-spring-boot` to version `0.2.31`. (#3185368)
- **JUnit**: Added `JUnit Jupiter` as a test dependency. (#eda32b5)

## Test Improvements
- **OpenFeign**: Adjusted and refined module test dependencies. (#2f7545f)

## Documentation
- **Readme**: Updated README with latest branch version information. (#e0a88cd)

## Build and Workflow Enhancements
- **Commons web dependencies**: Marked as non-optional for improved dependency management. (#434144d)
- **OpenFeign POM**: Cleaned up unnecessary whitespace. (#badca09)

---

For full details, refer to the [changelog](#).

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.2.22...0.2.23