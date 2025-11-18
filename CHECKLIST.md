# Kei Plugin Development Checklist

## ✅ Completed (v0.1-alpha)

### Core Infrastructure
- [x] Project structure (Gradle, Kotlin)
- [x] Plugin manifest (plugin.xml)
- [x] Data models (ApiContract, Endpoint, Schema, ValidationResult)
- [x] OpenAPI 3.x parser
- [x] Contract index service (caching, file watching)
- [x] Contract discovery on startup

### Validation Engine
- [x] TypeScript/JavaScript inspection
- [x] fetch() API call detection
- [x] axios API call detection
- [x] Request body validation
- [x] Required field checks
- [x] Type matching
- [x] Enum validation

### Quick Fixes
- [x] Add missing field fix
- [x] Default value generation

### UI Components
- [x] Tool Window (Contract Explorer)
- [x] Tree view of contracts & endpoints
- [x] Settings panel
- [x] Refresh action
- [x] Plugin icon

### Documentation
- [x] README with getting started
- [x] Feature list
- [x] Example usage
- [x] .gitignore

## 🚧 TODO for v0.2 (Next Sprint)

### GraphQL Support
- [ ] GraphQL schema parser
- [ ] GraphQL query/mutation detection
- [ ] GraphQL validation

### Python Support
- [ ] Python inspection (requests library)
- [ ] Python inspection (httpx library)
- [ ] Python type inference
- [ ] Python quick fixes

### Contract Diff Viewer
- [ ] Side-by-side diff UI
- [ ] Breaking change highlighter
- [ ] Impact analysis (affected files)
- [ ] Diff viewer tool window

### Mock Server
- [ ] Embedded Ktor server
- [ ] Mock data generation (Kotlin Faker)
- [ ] Request validation in mock
- [ ] Mock server UI panel
- [ ] Start/stop controls
- [ ] Request logs viewer

## 📋 TODO for v0.3

### Java/Kotlin Support
- [ ] RestTemplate inspection
- [ ] WebClient inspection
- [ ] Retrofit annotation parsing
- [ ] OkHttp inspection

### VCS Integration
- [ ] Pre-commit hook installer
- [ ] Breaking change dialog
- [ ] Git diff integration
- [ ] Historical contract tracking

### Test Generation
- [ ] JUnit test generator
- [ ] Pytest test generator
- [ ] Jest test generator
- [ ] Test template engine

## 🎯 TODO for v1.0

### Advanced Features
- [ ] Consumer-Driven Contract Testing
- [ ] Dependency graph builder
- [ ] Dependency graph visualization
- [ ] Impact analysis engine
- [ ] Circular dependency detection

### Performance
- [ ] Incremental indexing
- [ ] Async validation queue
- [ ] Cache optimization
- [ ] Large project handling (100k+ files)

### Polish
- [ ] Comprehensive error messages
- [ ] Better type inference
- [ ] Response validation
- [ ] Parameter validation
- [ ] Header validation
- [ ] Authentication/authorization hints

### Testing
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests
- [ ] Plugin test fixtures
- [ ] Performance benchmarks

### Documentation
- [ ] User guide
- [ ] API documentation
- [ ] Tutorial videos
- [ ] Blog posts

## 🔮 Future (v2.0+)

### AI-Powered Features
- [ ] Auto-generate OpenAPI from code
- [ ] Breaking change prediction
- [ ] Smart migration suggestions

### Multi-Protocol Support
- [ ] gRPC (proto files)
- [ ] SOAP (WSDL)
- [ ] AsyncAPI (WebSocket, Kafka)
- [ ] Thrift IDL

### Security & Performance
- [ ] Response time SLA validation
- [ ] Load testing integration
- [ ] OWASP API Security checks
- [ ] Rate limiting validation

### Integrations
- [ ] GitHub Actions integration
- [ ] GitLab CI integration
- [ ] Jenkins plugin
- [ ] Postman import/export

## 🐛 Known Issues

- [ ] Variable reference validation not implemented (MVP)
- [ ] Path parameter validation needs work
- [ ] Response validation not implemented yet
- [ ] Header validation not implemented yet

## 📝 Notes

**Development Approach**: Iterative, ship early & often
**Target**: Freemium model (free tier very usable, premium for teams)
**Focus**: Developer experience, speed, reliability

**Priority Order**:
1. Core validation (TypeScript) - DONE ✓
2. GraphQL + Python support
3. Mock server (critical for adoption)
4. VCS integration (enterprise feature)
5. Test generation
6. Advanced features

---

Last updated: 2024-11-18
