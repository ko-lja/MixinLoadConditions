package lu.kolja.mixinloadconditions.condition;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

/**
 * Lazily caches Forge's early loading mod ids.
 *
 * <p>The first lookup captures the mod ids exposed by {@link LoadingModList}
 * and stores them as a lowercase immutable set. Later evaluations reuse the
 * cached set because the loaded mod list is stable for the lifetime of the
 * game process.
 */
final class LoadedMods {
    private static volatile Set<String> loadedMods;

    private LoadedMods() {
    }

    /**
     * Returns the loaded Forge mod ids.
     *
     * @return lowercase immutable mod-id set
     */
    static Set<String> get() {
        Set<String> current = loadedMods;
        if (current != null) {
            return current;
        }

        synchronized (LoadedMods.class) {
            if (loadedMods == null) {
                LinkedHashSet<String> discoveredMods = LoadingModList.get().getMods().stream()
                        .map(ModInfo::getModId)
                        .map(modId -> modId.toLowerCase(Locale.ROOT))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                loadedMods = Collections.unmodifiableSet(discoveredMods);
            }
            return loadedMods;
        }
    }
}
