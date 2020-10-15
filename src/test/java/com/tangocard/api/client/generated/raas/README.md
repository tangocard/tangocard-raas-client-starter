## Summary

This suite of tests serves as examples for how to use the generated API clients classes.

### System Tests
These tests are "system tests", meaning they exercise behavior of a fully instantiated system. 
In other words, these tests run against Tango Card's live sandbox environment by default.

As such, please:

 - Remember, even though this looks like a tests of the generated API, it is really testing the Tango Card sandbox environment.
 - Do not run these tests in a tight loop (don't load test our sandbox infrastructure)
 - Please use your own sandbox credentials, instead of relying on the default out-of-the-box credentials.
    - The default credentials link to an account that can be changed without warning. (Don't depend on anything but your own sandbox account.)
