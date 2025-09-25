import { FundingCharacteristicEnum } from '../models/enums/funding-characteristic.enum';

export class FundingCharacteristicUtil {
  public static scopeCharacteristics(): FundingCharacteristicEnum[] {
    return [
      FundingCharacteristicEnum.INTERNATIONAL_PROGRAMME,
      FundingCharacteristicEnum.BILATERAL_PROGRAMME,
      FundingCharacteristicEnum.NATIONAL_PROGRAMME,
    ];
  }
  public static typeCharacteristics(): FundingCharacteristicEnum[] {
    return [
      FundingCharacteristicEnum.SCIENTIFIC_PROGRAMME,
      FundingCharacteristicEnum.COOPERATIVE_PROGRAMME,
      FundingCharacteristicEnum.MOBILITY_PROGRAMME,
    ];
  }

  public static projectFundingCharacteristics(): FundingCharacteristicEnum[] {
    return [
      FundingCharacteristicEnum.INDIVIDUAL_PROJECT,
      FundingCharacteristicEnum.CONSORTIUM,
    ];
  }

  public static fundingGoalCharacteristics(): FundingCharacteristicEnum[] {
    return [
      FundingCharacteristicEnum.PERSONAL_GRANT,
      FundingCharacteristicEnum.PROJECT_FUNDING,
      FundingCharacteristicEnum.INFRASTRUCTURE,
      FundingCharacteristicEnum.NETWORKING,
    ];
  }
}
