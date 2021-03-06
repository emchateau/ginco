/**
 * Copyright or © or Copr. Ministère Français chargé de la Culture
 * et de la Communication (2013)
 * <p/>
 * contact.gincoculture_at_gouv.fr
 * <p/>
 * This software is a computer program whose purpose is to provide a thesaurus
 * management solution.
 * <p/>
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p/>
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited liability.
 * <p/>
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systemsand/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 * <p/>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.mcc.ginco.tests.imports.ginco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fr.mcc.ginco.beans.CustomConceptAttributeType;
import fr.mcc.ginco.beans.SplitNonPreferredTerm;
import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.dao.ISplitNonPreferredTermDAO;
import fr.mcc.ginco.dao.IThesaurusConceptDAO;
import fr.mcc.ginco.dao.IThesaurusDAO;
import fr.mcc.ginco.exports.result.bean.GincoExportedThesaurus;
import fr.mcc.ginco.imports.ginco.GincoConceptImporter;
import fr.mcc.ginco.imports.ginco.GincoCustomAttributeImporter;

public class GincoConceptImporterTest {

	@Mock
	private IThesaurusDAO thesaurusDAO;

	@Mock
	private IThesaurusConceptDAO thesaurusConceptDAO;
	

	@Mock
	private ISplitNonPreferredTermDAO splitNonPreferredTermDAO;
	
	@Mock
	private GincoCustomAttributeImporter gincoCustomAttributeImporter;

	@InjectMocks
	GincoConceptImporter gincoConceptImporter;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testStoreConcepts() {

		Thesaurus th1 = new Thesaurus();
		th1.setIdentifier("http://th1");

		ThesaurusConcept c1 = new ThesaurusConcept();
		c1.setIdentifier("http://c1");
		c1.setThesaurus(th1);

		List<ThesaurusConcept> resultedConcepts = new ArrayList<ThesaurusConcept>();
		List<ThesaurusConcept> concepts = new ArrayList<ThesaurusConcept>();
		concepts.add(c1);

		GincoExportedThesaurus exportedThesaurus = new GincoExportedThesaurus();
		exportedThesaurus.setThesaurus(th1);
		exportedThesaurus.setConcepts(concepts);

		Map<String, CustomConceptAttributeType> savedTypes = new HashMap<String, CustomConceptAttributeType>();
		Mockito.when(
				thesaurusConceptDAO.update(Mockito.any(ThesaurusConcept.class)))
				.thenReturn(c1);
		resultedConcepts = gincoConceptImporter
				.storeConcepts(exportedThesaurus, exportedThesaurus.getThesaurus(),savedTypes );

		Assert.assertEquals(resultedConcepts.size(), concepts.size());
		Assert.assertEquals(resultedConcepts.get(0).getIdentifier(), concepts
				.get(0).getIdentifier());
	}
	
	@Test
	public void testStoreComplexConcepts() {

		Thesaurus th1 = new Thesaurus();
		th1.setIdentifier("http://th1");

		SplitNonPreferredTerm cc1 = new SplitNonPreferredTerm();
		cc1.setIdentifier("http://cc1");
		cc1.setThesaurus(th1);

		List<SplitNonPreferredTerm> resultedComplexConcepts = new ArrayList<SplitNonPreferredTerm>();
		List<SplitNonPreferredTerm> complexConcepts = new ArrayList<SplitNonPreferredTerm>();
		complexConcepts.add(cc1);

		GincoExportedThesaurus exportedThesaurus = new GincoExportedThesaurus();
		exportedThesaurus.setThesaurus(th1);
		exportedThesaurus.setComplexConcepts(complexConcepts);

		Mockito.when(
				splitNonPreferredTermDAO.update(Mockito.any(SplitNonPreferredTerm.class)))
				.thenReturn(cc1);
		resultedComplexConcepts = gincoConceptImporter.storeComplexConcepts(exportedThesaurus.getComplexConcepts(), exportedThesaurus.getThesaurus());

		Assert.assertEquals(resultedComplexConcepts.size(), complexConcepts.size());
		Assert.assertEquals(resultedComplexConcepts.get(0).getIdentifier(), complexConcepts
				.get(0).getIdentifier());
	}
}
