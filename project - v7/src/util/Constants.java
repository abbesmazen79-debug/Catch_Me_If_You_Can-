package util;

public class Constants {

    // Window
    public static final String TITLE         = "CATCH ME IF YOU CAN !";
    public static final int    WINDOW_WIDTH  = 1280;
    public static final int    WINDOW_HEIGHT = 720;

    // Tile / Map
    public static final int TILE_SIZE = 32;
    public static final int HUD_HEIGHT = 50;
    public static final int MAP_COLS  = WINDOW_WIDTH  / TILE_SIZE;          // 40
    public static final int MAP_ROWS  = (WINDOW_HEIGHT - HUD_HEIGHT) / TILE_SIZE; // 20

    // Game loop
    public static final int  TARGET_FPS   = 60;
    public static final long OPTIMAL_TIME = 1_000_000_000L / TARGET_FPS;

    // Player
    public static final int   PLAYER_SIZE  = 22;
    public static final int   PLAYER_SPEED = 3;
    public static final float SLIDE_SPEED  = 4f; // px/tick — 32/4 = 8 ticks per tile

    public static final float DASH_SLIDE_SPEED = 16f; 
    public static final float NORMAL_SLIDE_SPEED = 6f;
    public static final long  DASH_COOLDOWN = 2_000_000_000L; // 2 seconds in nanoseconds

    // Enemy
    public static final int ENEMY_SIZE = 24;

    // Food
    public static final int  FOOD_SIZE          = 12;
    public static final int  FOOD_NORMAL_SCORE  = 10;
    public static final int  FOOD_SPECIAL_SCORE = 50;
    public static final int  FOOD_COUNT         = 30;
    public static final int  SPECIAL_FOOD_COUNT = 5;

    // Power-up duration (ms)
    public static final long POWER_DURATION = 5_000;

    //environmental tips that appear when the game is over
    public static final String[] RSETIPS={"Saving energy helps reduce pollution and conserves natural resources. Simple actions like turning off unused devices, using energy-efficient equipment, and relying on renewable energy sources can make a big difference",
    "Reducing waste is one of the most effective ways to protect the environment. Companies and individuals should focus on minimizing the amount of trash they produce by reusing materials and recycling whenever possible",
    "Conserving water is crucial for sustaining life on Earth. People can save water by fixing leaks, using water-efficient appliances, and being mindful of their water usage in daily activities like showering and watering plants",
    "Protecting natural habitats is essential for preserving biodiversity. We should support conservation efforts, avoid deforestation, and promote sustainable land use practices to ensure that wildlife and ecosystems thrive",
    "Using public transportation, carpooling, biking, or walking instead of driving alone can significantly reduce carbon emissions and help combat climate change. Choosing greener transportation options is a simple yet impactful way to contribute to a healthier planet",
    "Supporting sustainable agriculture is important for the environment. Choosing locally sourced, organic, and plant-based foods can reduce the carbon footprint of our diets and promote more sustainable farming practices", 
    "Advocating for environmental policies and supporting organizations that work towards sustainability can create positive change on a larger scale. By raising awareness and encouraging others to take action, we can collectively make a significant impact in protecting our planet",
    "Using environmentally friendly materials reduces harm to nature and supports sustainability. This involves choosing biodegradable, recyclable, or renewable materials instead of harmful ones like plastic",
    "Responsible consumption means using resources wisely and avoiding unnecessary waste. It encourages people to buy only what they need and to choose sustainable products. This mindset helps reduce environmental pressure and promotes long-term resource availability.",
    "Protecting wildlife is essential for maintaining ecological balance. Human activities often threaten animal habitats, so it is important to take steps to preserve them. Supporting conservation efforts and reducing pollution can help ensure that wildlife continues to thrive.",
    "Access to clean water is vital for life. Pollution from waste, chemicals, and plastics can contaminate water sources, making them unsafe. Efforts to clean rivers, lakes, and oceans are crucial to protect both human health and aquatic ecosystems.",
    "Planting trees is a simple yet powerful way to combat climate change. Trees absorb carbon dioxide, provide oxygen, and create habitats for wildlife. By planting more trees, we can help reduce the effects of global warming and improve air quality",
    "Educating others about environmental issues is key to creating a sustainable future. Sharing knowledge about the importance of conservation, pollution reduction, and sustainable living can inspire more people to take action and make a positive impact on the planet",
    "Supporting renewable energy sources like solar and wind power can help reduce our reliance on fossil fuels, which are a major contributor to climate change. Investing in clean energy is essential for a sustainable future",
    "Your carbon imprint is equivalent to the emissions produced by driving a car for 100 miles. Consider using public transportation or carpooling to reduce your carbon footprint."
};

    //l'emreinte carbone 
    public static final double normal_carbon_imprint = 0.00009;
    public static final double dashing_carbon_imprint = 0.0005;
}