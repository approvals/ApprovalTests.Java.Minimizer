package org.approvaltests.partial;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TreeShakerTest
{
  @Test
  void shakeStarterProject()
  {
    SelectionConfig config = SelectionConfig.load(new File("src/test/resources/starter-project-selection.json"));
    File sibling = new File("../ApprovalTests.Java").getAbsoluteFile().toPath().normalize().toFile();
    String repository = new File(sibling, ".git").exists() ? "file://" + sibling : SourceCheckout.REPOSITORY;
    File checkout = SourceCheckout.cloneTag(repository, config.approvaltestsVersion,
        new File(System.getProperty("java.io.tmpdir"), "approvaltests-partial-test/approvaltests-" + config.approvaltestsVersion));
    JarBuilder.build(checkout);
    Approvals.verify(TreeShaker.shake(checkout, config.entryPoints, JarBuilder.thirdPartyJars(checkout)));
  }
}
