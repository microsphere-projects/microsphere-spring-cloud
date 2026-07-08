# Release Notes

## v0.1.11

# Release Notes for Version 0.1.11

## New Features
- CI workflows enhanced to improve Maven processes and automate release notes generation. ([a9fe65c](https://github.com/mercyblitz/your-repository-name/commit/a9fe65c))

## Other Changes
- Upgraded `microsphere-spring-boot` dependency to version 0.1.12. ([d8f5e93](https://github.com/mercyblitz/your-repository-name/commit/d8f5e93))
- Updated README to reflect the latest branch versions. ([f281184](https://github.com/mercyblitz/your-repository-name/commit/f281184))
- Prepared for the next patch iteration by bumping version after 0.1.10. ([5fd328a](https://github.com/mercyblitz/your-repository-name/commit/5fd328a)) 

--- 

## v0.1.12

# Release Notes for Version 0.1.12

## Dependency Updates
- Bumped `microsphere-spring-boot` to `0.1.13`. ([cd39772](https://github.com/mercyblitz/microsphere-spring-cloud/commit/cd39772))
- Updated `JUnit Jupiter` to version `5.14.4`. ([9e40411](https://github.com/mercyblitz/microsphere-spring-cloud/commit/9e40411))
- Upgraded parent project version to `0.2.9`. ([9319838](https://github.com/mercyblitz/microsphere-spring-cloud/commit/9319838))

## Build and Workflow Enhancements
- Configured Maven server authentication and introduced Maven wrapper. ([fe02ac5](https://github.com/mercyblitz/microsphere-spring-cloud/commit/fe02ac5))
- Adjusted CI setup for Java and transitioned to Maven wrapper usage. ([5c84be7](https://github.com/mercyblitz/microsphere-spring-cloud/commit/5c84be7))

## Documentation
- Enhanced release notes and improved release creation workflows. ([00e8170](https://github.com/mercyblitz/microsphere-spring-cloud/commit/00e8170))

## Other Changes
- Updated version to prepare for the next development iteration after `0.1.11`. ([4d79072](https://github.com/mercyblitz/microsphere-spring-cloud/commit/4d79072))
- Merged `release-1.x` branch into `dev-1.x`. ([a0d0961](https://github.com/mercyblitz/microsphere-spring-cloud/commit/a0d0961))  

---

For a detailed list of changes, see the [Full Changelog](https://github.com/mercyblitz/microsphere-spring-cloud/compare/0.1.11...0.1.12).  

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.11...0.1.12## v0.1.13

# Release Notes: Version 0.1.13

## Documentation
- Updated README with a Table of Contents (TOC), usage, and documentation details. ([5f994c8](<commit-link>))
- Improved README formatting and clarified branch naming conventions. ([9309569](<commit-link>))

## Build and Workflow Enhancements
- Added `.github` prompt templates for improved workflow automation. ([32aff6f](<commit-link>))
- Merged updates from `release-1.x` and upstream `microsphere-projects:dev-1.x`. ([b9a2076](<commit-link>), [bb02189](<commit-link>))

## Dependency Updates
- Updated `microsphere-spring-boot.version` to `0.1.14`. ([94b315d](<commit-link>))

---

For a detailed commit history, refer to the full changelog.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.12...0.1.13## v0.1.14

# Release Notes - Version 0.1.14

## New Features
- Adopted `microsphere` collection factories for improved consistency. (#947e789)

## Dependency Updates
- Bumped `microsphere-spring-boot` dependency to 0.1.16. (#09f4726, #29a36aa)
- Upgraded parent version to 0.3.0. (#0f06438)

## Documentation
- Updated README to reflect the latest branch and version tables. (#c39d353, #4b537cf)
- Adjusted README branch versions to align with 0.1.14 and 0.2.14 releases. (#536f17a)

## Code Quality Improvements
- Removed trailing newlines in 146 Java source files. (#7eb224a)
- Cleaned up duplicated line separators and trailing whitespaces. (#e60a7b3)
- Reformatted code to align with consistent indentation and import structure. (#d0046eb)

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` for better workflow alignment. (#b7a9b6c)
- Incremented version to prepare for 0.1.14 release. (#76152b5)

---

**Commit Reference:** [View Full Changelog](https://github.com/mercyblitz/microsphere-spring-cloud/compare/0.1.13...0.1.14)

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.13...0.1.14## v0.1.15

# Release Notes for v0.1.15

## Dependency Updates
- Bumped `microsphere-spring-boot` to version `0.1.17`. ([2d9e4ed](https://github.com/mercyblitz/microsphere-spring-cloud/commit/2d9e4ed))

## Documentation
- Fixed list indentation in `README.md`. ([27cd979](https://github.com/mercyblitz/microsphere-spring-cloud/commit/27cd979))
- Updated documentation to reflect version bumps. ([27cd979](https://github.com/mercyblitz/microsphere-spring-cloud/commit/27cd979))

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x`. ([d1b555c](https://github.com/mercyblitz/microsphere-spring-cloud/commit/d1b555c))
- Bumped version to next patch after publishing v0.1.14. ([cff7f3f](https://github.com/mercyblitz/microsphere-spring-cloud/commit/cff7f3f))

---

Full Changelog: [v0.1.14...v0.1.15](https://github.com/mercyblitz/microsphere-spring-cloud/compare/v0.1.14...v0.1.15)

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.14...0.1.15## v0.1.16

# Release Notes - Version 0.1.16

## Dependency Updates
- Upgraded `microsphere-spring-boot` to version **0.1.18**.  
- Updated `microsphere-build` parent to version **0.3.1**.

## Documentation
- Updated README to reflect the latest versions.

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` to synchronize branches.

## Other Changes
- Bumped the patch version for post-0.1.15 release preparation.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.15...0.1.16## v0.1.17

# Release Notes - Version 0.1.17

## New Features
- **ConditionalOnFeaturesAvailable**: Added new annotation for conditional beans based on feature availability. (#904380b)  
- **Auto-Configuration for HasFeatures**: Introduced configuration support to enable feature detection from properties. (#9cbbf19)

## Documentation
- Updated version compatibility information in the README. (#0e64e3a)

## Dependency Updates
- Upgraded `microsphere-spring-boot` dependency to version 0.1.19. (#65781ef)

## Build and Workflow Enhancements
- Bumped patch version post-release of 0.1.16. (#3f7abbe)  
- Merged `release-1.x` into `dev-1.x` for synchronization. (#973349c)

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.16...0.1.17## v0.1.18

# Release Notes - Version 0.1.18

### New Features
- Added support for discovery availability conditional annotations. ([6136830](#))
- Introduced auto-registration condition and web modules. ([a83c9af](#))
- Adopted name-based `AutoConfigureAfter` for improved web configurations. ([a94a820](#))

### Dependency Updates
- Upgraded `microsphere-build` parent to version `0.3.4`. ([eb742af](#))
- Bumped `microsphere-spring-boot` to version `0.1.20`. ([dcb6186](#))
- Updated parent POM version to `0.3.3`. ([1abbcfb](#))

### Documentation
- Updated README to reflect new branch versions. ([733d1d3](#))

### Build and Workflow Enhancements
- Updated POMs: version bump and refined dependencies. ([47b6771](#))
- Merged branch `release-1.x` into `dev-1.x`. ([05e4173](#))

---

**Note**: For the complete list of changes, please refer to the commit history.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.17...0.1.18## v0.1.19

# Release Notes for v0.1.19

## Dependency Updates
- Bumped `microsphere-spring-boot` to version `0.1.22`. [5020895]

## Documentation
- Updated `README` to reflect the latest versions. [95d6eff]

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` for branch alignment. [d66d3c6]
- Updated post-release version to prepare for the next patch cycle. [af3cd39]

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.18...0.1.19## v0.1.20

# Release Notes: Version 0.1.20

## New Features
- **FeaturesProperties Introduced**: Added `FeaturesProperties` utility for managing feature-related configurations. ([2aca15c](https://example.com))
- **Default Feature Registration**: Spring Cloud Commons default features are now registered automatically. ([f6a4d24](https://example.com))
- **Feature Naming Enhancements**: FeaturesUtils now uses colon (`:`) as the separator for feature names. ([296f84a](https://example.com))

## Bug Fixes
- Cleaned up unused Javadoc links and imports. ([e992ff1](https://example.com))
- Adjusted expected feature names in tests. ([2bed908](https://example.com))

## Documentation
- Updated README to reflect branch version table changes. ([917ac13](https://example.com))

## Dependency Updates
- Upgraded `microsphere-spring-boot` to versions `0.1.23`, `0.1.24`, `0.1.25`, and finally `0.1.27`. ([945bd32](https://example.com), [7700498](https://example.com), [d152e33](https://example.com), [5f8e140](https://example.com))
- Bumped `microsphere-build` parent to `0.3.5`. ([ecb37ad](https://example.com))

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` branch for synchronization. ([bc033b9](https://example.com))
- Updated project version to next patch after publishing `0.1.19`. ([d1fd478](https://example.com))

---

For full details of the changes included in this release, please refer to the [Full Changelog](https://example.com).

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.19...0.1.20## v0.1.21

# Release Notes for Version 0.1.21

## 🚀 New Features
- **OpenFeign Support:** Added feature class mappings to enhance OpenFeign integration. (#141)

## 📝 Documentation
- Updated `README` to reflect branch release version changes. (b94c7f8)

## 📦 Dependency Updates
- Upgraded `microsphere-spring-boot` BOM to version 0.1.28. (f771442)

## 🔧 Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` for synchronization. (0032d8f)
- Bumped version to 0.1.21 following the release of 0.1.20. (0da83b5)

---

For detailed changes, check the [Full Changelog](https://github.com/mercyblitz/project-name/compare/0.1.20...0.1.21).

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.20...0.1.21## v0.1.22

# Release Notes for Version 0.1.22

## New Features
- Added support for Feign auto-refresh with customizer and improved ordering ([7f09bfb], [4c39928]).
- Introduced conditional annotations for OpenFeign: `@ConditionalOnOpenFeignAvailable` and `@ConditionalOnOpenFeignEnabled` ([fc73e41], [4c82ac0]).
- Added optional Microsphere annotation processor ([9684774]).

## Bug Fixes
- Hardened OpenFeign auto-configuration class conditions ([dffe6b0]).
- Improved feature auto-configuration to handle classpath dependencies more effectively ([0e43d3e], [d9f7d53], [147d975]).
- Guarded multiple service registry and auto-registration configurations by classpath or bean presence ([57d051e], [79588cd], [052a02a]).

## Documentation
- Updated README with latest branch versions ([d9a8ba8]).
- Improved Javadoc consistency with standardized headings and example blocks ([7cf912a], [138cc57]).
- Enhanced Javadoc clarification for integration test classes ([ceb651d]).

## Dependency Updates
- Upgraded `microsphere-spring-boot` to `0.1.30` ([5cf5f20]).
- Updated `microsphere-build` parent to versions `0.3.6`, `0.3.7`, and `0.3.8` ([1b2f7d0], [2942487], [1033366]).

## Test Improvements
- Added comprehensive condition tests for feature auto-configurations ([aee75ba]).
- Improved coverage and integration tests for service endpoints, registry, and auto-registration configurations ([52736df], [c62b5fe], [158db7f], [7a92168]).
- Standardized test setup to use `NONE` as the web environment where applicable ([a94182b], [111dda1]).
- Refactored and renamed test classes for clarity and improved scope definitions ([3096606], [24c52ba], [862e429], [ba84a43]).

## Build and Workflow Enhancements
- Introduced Maven Wrapper to CI build workflow ([9ea1165]).
- Adjusted CI workflow to use `mvn` commands ([8629a90]).
- Normalized whitespace in commons POM ([1da4d7d]).
- Refined Microsphere dependencies for the commons module ([ea95293]).

## Other Changes
- Cleaned up Javadoc in Tomcat fault tolerance auto-configuration ([037c5b1]).
- Inline Feign and OpenFeign class references for clarity ([39d9bb0], [54ee2a8]).

---

**Full Changelog:** Available in the project repository.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-spring-cloud/compare/0.1.21...0.1.22