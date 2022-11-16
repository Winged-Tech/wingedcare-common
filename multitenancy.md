# Designing multi-tenancy enabled microservice applications on top of JHipster

Designing for multi-tenancy is a common requirement for SaaS applications, and it brings a lot of challenges when facing a microservice architecture. We have built a multi-tenancy module on top of JHipster microservice stack, which supports tenant data isolation on database level - Each of the tenants is allocated a separate physical database  for storing their data. Currently, the module is designed to work with JHipster gateway, JHipster UAA and JHipster microservice applications. In this paper, we're going to discuss how we address each of the multi-tenant challenges in a microservice architecture.

* Making all API requests multi-tenant aware
* Multi-tenant aware security - OAuth2 tokens, request interceptors and more  
* Multi-tenant aware data sources and database migrations (right now only MongoDB is supported out of all DB technologies)
* Highly-configurable - turning on/off multi-tenancy mode, different domains for different tenants 
* Multi-tenant aware caches
* Multi-tenant aware feign clients
* Multi-tenant aware configuration
* Minimized invasion to application code with auto configuration

The solution has been tested in production applications, although there are still a lot of improvement to go.
