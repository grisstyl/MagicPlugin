package com.elmakers.mine.bukkit.action;

import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.action.SpellAction;
import com.elmakers.mine.bukkit.api.spell.Spell;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

public class ActionContext implements Cloneable {
    private final ConfigurationSection parameters;
    private final SpellAction action;

    public ActionContext(SpellAction action, ConfigurationSection actionParameters)
    {
        this.action = action;
        this.parameters = actionParameters;
    }

    public void initialize(Spell spell, ConfigurationSection baseParameters)
    {
        action.initialize(spell, getEffectiveParameters(baseParameters));
    }

    public void prepare(CastContext context, ConfigurationSection parameters)
    {
        action.prepare(context, getEffectiveParameters(parameters));
    }

    public SpellResult perform(CastContext context)
    {
        boolean hasTarget = context.getTargetLocation() != null;
        boolean hasEntityTarget = context.getTargetEntity() != null;
        if (action.requiresTarget() && !hasTarget) return SpellResult.NO_TARGET;
        if (action.requiresTargetEntity() && !hasEntityTarget) return SpellResult.NO_TARGET;

        return action.perform(context);
    }

    public ConfigurationSection getActionParameters()
    {
        return parameters;
    }

    public ConfigurationSection getEffectiveParameters(ConfigurationSection baseParameters)
    {
        ConfigurationSection effectiveParameters = baseParameters;
        if (this.parameters != null || baseParameters == null || baseParameters.contains("actions")) {
            effectiveParameters = new MemoryConfiguration();
            ConfigurationUtils.addConfigurations(effectiveParameters, baseParameters);
            effectiveParameters.set("actions", null);
            ConfigurationUtils.addConfigurations(effectiveParameters, this.parameters);
        }

        return effectiveParameters;
    }

    public SpellAction getAction() {
        return this.action;
    }

    public void finish(CastContext context) {
        action.finish(context);
    }

    @Override
    public Object clone()
    {
        return new ActionContext((SpellAction)action.clone(), parameters);
    }
}
