package at.ac.tuwien.fundify.domain.funding.vo.enums;

public enum EEntryOrigin {
  /**
   * Indicates that the entity has been created via the REFOP/Fundify frontend.
   */
    REFOP,
  /**
   * Indicates that the entry has been created via the automatic sync from the RIS API of the funder.
   */
    ENDPOINT
}
