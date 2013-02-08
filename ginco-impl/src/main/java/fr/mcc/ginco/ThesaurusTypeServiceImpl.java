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
package fr.mcc.ginco;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import fr.mcc.ginco.beans.ThesaurusType;
import fr.mcc.ginco.dao.IGenericDAO;
import fr.mcc.ginco.dao.hibernate.GenericHibernateDAO;
import fr.mcc.ginco.log.Log;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dabel
 *
 */
@Transactional
@Service("thesaurusTypeService")
public class ThesaurusTypeServiceImpl implements IThesaurusTypeService {
	
	
	@Log private Logger log;
	    
	@Inject
	@Named("thesaurusTypeDAO")
    private IGenericDAO<ThesaurusType, Integer> thesaurusTypeDAO;
	
	/* (non-Javadoc)
	 * @see fr.mcc.ginco.IThesaurusTypeService#getThesaurusTypeList()
	 */
	@Override
	public List<ThesaurusType> getThesaurusTypeList() {
		return thesaurusTypeDAO.findAll();
	}

    /* (non-Javadoc)
	 * @see fr.mcc.ginco.IThesaurusTypeService#getThesaurusTypeById()
	 */
    @Override
    public ThesaurusType getThesaurusTypeById(Integer id) {
        return thesaurusTypeDAO.getById(id);
    }


    public IGenericDAO<ThesaurusType, Integer> getThesaurusTypeDAO() {
		return thesaurusTypeDAO;
	}
	
}
