package android.support.v7.graphics;

import android.graphics.Color;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ColorCutQuantizerTest {

    @Test
    public void shouldIgnoreBlackAndWhite() {
        ColorHistogram mockHistogram = MockHistogramFactory
                .initWithColor(Color.WHITE, 10)
                .andColor(Color.BLACK, 10)
                .andColor(Color.GREEN, 10)
                .mock();

        ColorCutQuantizer quantizer = new ColorCutQuantizer(mockHistogram, 16);
        List<Palette.Swatch> swatches = quantizer.getQuantizedColors();
        assertThat(swatches)
                .containsExactly(new Palette.Swatch(Color.GREEN, 10));
    }

    @Test
    public void shouldIgnoreTransparentColors() {
        ColorHistogram mockHistogram = MockHistogramFactory
                .initWithColor(Color.GREEN, 10)
                .andColor(Color.argb(0, 0, 0, 0), 10) // Fully transparent black
                .andColor(Color.argb(0, 0, 0, 255), 10) // Fully transparent red
                .mock();

        ColorCutQuantizer quantizer = new ColorCutQuantizer(mockHistogram, 16);
        List<Palette.Swatch> swatches = quantizer.getQuantizedColors();
        assertThat(swatches)
                .containsExactly(new Palette.Swatch(Color.GREEN, 10));
    }

    static class MockHistogramFactory {
        HashMap<Integer, Integer> colorOccurences = new HashMap<>();

        public static MockHistogramFactory initWithColor(int color, int occurenceCount) {
            MockHistogramFactory factory = new MockHistogramFactory();
            return factory.andColor(color, occurenceCount);
        }

        public MockHistogramFactory andColor(int color, int occurenceCount) {
            colorOccurences.put(color, occurenceCount);

            return this;
        }

        public ColorHistogram mock() {
            int[] colors = new int[colorOccurences.size()];
            int[] colorCounts = new int[colorOccurences.size()];

            int index = 0;
            for (Map.Entry<Integer, Integer> colorOccurence : colorOccurences.entrySet()) {
                colors[index] = colorOccurence.getKey();
                colorCounts[index] = colorOccurence.getValue();

                index++;
            }

            return new MockHistogram(colors, colorCounts);
        }
    }

    static class MockHistogram extends ColorHistogram {

        private final int[] colors;
        private final int[] colorCounts;

        MockHistogram(int[] colors, int[] colorCounts) {
            super(new int[0]);
            this.colors = colors;
            this.colorCounts = colorCounts;
        }

        @Override
        int getNumberOfColors() {
            return colors.length;
        }

        @Override
        int[] getColors() {
            return colors;
        }

        @Override
        int[] getColorCounts() {
            return colorCounts;
        }
    }

}
