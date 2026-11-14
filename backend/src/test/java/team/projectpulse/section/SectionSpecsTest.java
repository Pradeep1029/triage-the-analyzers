package team.projectpulse.section;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SectionSpecsTest {

    /**
     * The database lowercases section_name the same way on every server, so the search term must be lowercased the
     * same way too. Under a Turkish default locale, String.toLowerCase() maps 'I' to dotless 'ı', the pattern becomes
     * "%sectıon ı%", and a search for "SECTION I" no longer finds the course section named "Section I".
     */
    @Test
    @SuppressWarnings("unchecked")
    void containsSectionNameLowercasesTheSearchTermIndependentOfTheServerLocale() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            Root<Section> root = mock(Root.class);
            CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
            Expression<String> lowerSectionName = mock(Expression.class);
            given(criteriaBuilder.lower(any())).willReturn(lowerSectionName);

            SectionSpecs.containsSectionName("SECTION I").toPredicate(root, mock(CriteriaQuery.class), criteriaBuilder);

            ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
            verify(criteriaBuilder).like(eq(lowerSectionName), pattern.capture());
            assertThat(pattern.getValue()).isEqualTo("%section i%");
        } finally {
            Locale.setDefault(saved);
        }
    }

}
