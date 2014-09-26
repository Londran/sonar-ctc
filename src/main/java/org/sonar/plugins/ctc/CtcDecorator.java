package org.sonar.plugins.ctc;

import org.sonar.api.batch.CoverageExtension;

import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.batch.Decorator;

public class CtcDecorator implements Decorator, CoverageExtension {

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {
    // TODO Auto-generated method stub

  }

}
