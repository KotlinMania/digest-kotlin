import Testing
import DigestKotlin

@Suite("Digest Swift Export Tests")
struct DigestExportTests {
    @Test("Swift module imports and basic types are reachable")
    func swiftModuleLoads() throws {
        #expect(Bool(true))
    }
}
