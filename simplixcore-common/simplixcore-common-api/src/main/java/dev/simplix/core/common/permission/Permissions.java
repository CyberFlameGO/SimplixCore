package dev.simplix.core.common.permission;

import de.leonhard.storage.util.FileUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import dev.simplix.core.common.ApplicationInfo;
import dev.simplix.core.common.aop.ContextAware;

/**
 * Manager class for your {@link Permission}
 * <p>
 * With this class you can store & registered permissions
 */
@ContextAware
@UtilityClass
public class Permissions {

  private ApplicationInfo APPLICATION_CONTEXT;

  private final List<Permission> registeredPermissions = new ArrayList<>();

  public List<Permission> registeredPermissions() {
    return Collections.unmodifiableList(Permissions.registeredPermissions);
  }

  public void register(@NonNull final Permission permission) {
    Permissions.registeredPermissions.add(permission);
  }

  public void registerAll(@NonNull final List<Permission> permissions) {
    Permissions.registeredPermissions.addAll(permissions);
  }

  @SneakyThrows
  public <T> void addFromClass(final Class<?> clazz) {
    for (final Field field : clazz.getFields()) {
      if (field.getType() != Permission.class)
        continue;

      final Permission perm = (Permission) field.get(null);
      if (perm == null)
        continue;
      Permissions.register(perm);
    }
  }

  public void writeToFile() {
    final File dataFile = FileUtils.getAndMake(
        APPLICATION_CONTEXT.name() + ".perms",
        APPLICATION_CONTEXT.workingDirectory().getAbsolutePath());

    final List<String> out = new ArrayList<>(Arrays.asList(
        "# "
        + APPLICATION_CONTEXT.name()
        + " v."
        + APPLICATION_CONTEXT.version(),
        "# This file lists all permissions we use.",
        "# You can also view them using our menu system.",
        "# ",
        "# Do not change them! Please use the settings.yml instead.",
        ""
    ));

    for (final val perm : Permissions.registeredPermissions())
      out.add(perm.permission() + ";" + perm.type() + ";" + String
          .join(",", perm.description()));

    FileUtils.write(dataFile, out);
  }
}
