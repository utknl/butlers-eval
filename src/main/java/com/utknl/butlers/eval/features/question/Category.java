package com.utknl.butlers.eval.features.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {
    // --- SCIENCE & TECH ---
    SPACE_EXPLORATION("Astronomy, planetary science, and the history of space missions."),
    BIOLOGY_HEALTH("Human anatomy, genetics, nutrition, and medical breakthroughs."),
    PHYSICS_CHEMISTRY("Fundamental laws of nature, matter, energy, and chemical reactions."),
    CYBERSECURITY("Digital privacy, hacking, encryption, and safe internet practices."),
    ARTIFICIAL_INTELLIGENCE("Machine learning, neural networks, ethics in AI, and LLMs."),
    ENVIRONMENT_CLIMATE("Ecology, sustainability, renewable energy, and climate change."),

    // --- CULTURE & SOCIETY ---
    WORLD_HISTORY("Ancient civilizations, world wars, and major historical turning points."),
    MODERN_LITERATURE("Classic and contemporary books, authors, and literary movements."),
    CINEMA_TV("Film history, directing techniques, and influential television series."),
    MUSIC_THEORY("Musical genres, instruments, composition, and famous composers."),
    PHILOSOPHY("Ethics, logic, existentialism, and historical philosophical schools."),
    MYTHOLOGY("Folklore and myths from Greek, Norse, Egyptian, and Asian cultures."),

    // --- PERSONAL & LIFESTYLE ---
    PERSONAL_FINANCE("Budgeting, investing, taxes, and economic literacy."),
    PSYCHOLOGY("Human behavior, cognitive biases, mental health, and emotions."),
    GASTRONOMY("Culinary history, world cuisines, cooking techniques, and nutrition."),
    FITNESS_WELLNESS("Exercise science, yoga, sleep hygiene, and physical health."),
    MINDFULNESS("Meditation, stress management, and emotional intelligence."),
    TRAVEL_CULTURE("Global traditions, languages, and significant world landmarks."),

    // --- PROFESSIONAL & PRACTICAL ---
    ENTREPRENEURSHIP("Startup culture, business models, and leadership strategies."),
    SOFT_SKILLS("Communication, conflict resolution, and effective teamwork."),
    MARKETING_BRANDING("Consumer behavior, advertising history, and digital marketing."),
    DATA_ANALYSIS("Statistics, data visualization, and evidence-based decision making."),

    // --- GLOBAL & CURRENT AFFAIRS ---
    GEOPOLITICS("International relations, global trade, and political systems."),
    SOCIOLOGY("Social structures, urbanization, and cultural shifts in the 21st century."),
    ECONOMICS("Macroeconomics, supply and demand, and global markets."),
    LAW_ETHICS("Legal systems, human rights, and corporate responsibility.");

    private final String categoryHint;

}
