package com.pocketkhata.app;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProfileValidationTest {
  @Test
  public void nameAcceptsBanglaAndEnglishButRejectsBlankOrOversizedInput() {
    assertNull(ProfileValidation.nameError("Md Abu Musa"));
    assertNull(ProfileValidation.nameError("আবু মুসা"));
    assertNotNull(ProfileValidation.nameError("   "));
    assertNotNull(ProfileValidation.nameError("A"));
    assertNotNull(ProfileValidation.nameError("a".repeat(61)));
  }

  @Test
  public void phoneAcceptsLocalAndInternationalFormatsAndRejectsInvalidNumbers() {
    assertNull(ProfileValidation.phoneError("01723 000000"));
    assertNull(ProfileValidation.phoneError("+880 (1723) 000-000"));
    assertEquals("+8801723000000", ProfileValidation.normalizePhone("+880 (1723) 000-000"));
    assertNull(ProfileValidation.phoneError(""));
    for (String value :
        new String[] {"56895", "+880hello", "++8801723000000", "1234567890123456"}) {
      assertNotNull(value, ProfileValidation.phoneError(value));
    }
  }
}
