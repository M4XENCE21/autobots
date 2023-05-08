package autobots.connectors.accountFake;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;

/**
 * Account information.
 */
public class AccountFake {

	/**
	 * Maker commission.
	 */
	private int makerCommission;

	/**
	 * Taker commission.
	 */
	private int takerCommission;

	/**
	 * Buyer commission.
	 */
	private int buyerCommission;

	/**
	 * Seller commission.
	 */
	private int sellerCommission;

	/**
	 * Whether or not this account can trade.
	 */
	private boolean canTrade;

	/**
	 * Whether or not it is possible to withdraw from this account.
	 */
	private boolean canWithdraw;

	/**
	 * Whether or not it is possible to deposit into this account.
	 */
	private boolean canDeposit;

	/**
	 * Last account update time.
	 */
	private long updateTime;

	/**
	 * List of asset balances of this account.
	 */
	private List<AssetBalanceFake> balances;

	public int getMakerCommission() {
		return makerCommission;
	}

	public void setMakerCommission(int makerCommission) {
		this.makerCommission = makerCommission;
	}

	public int getTakerCommission() {
		return takerCommission;
	}

	public void setTakerCommission(int takerCommission) {
		this.takerCommission = takerCommission;
	}

	public int getBuyerCommission() {
		return buyerCommission;
	}

	public void setBuyerCommission(int buyerCommission) {
		this.buyerCommission = buyerCommission;
	}

	public int getSellerCommission() {
		return sellerCommission;
	}

	public void setSellerCommission(int sellerCommission) {
		this.sellerCommission = sellerCommission;
	}

	public boolean isCanTrade() {
		return canTrade;
	}

	public void setCanTrade(boolean canTrade) {
		this.canTrade = canTrade;
	}

	public boolean isCanWithdraw() {
		return canWithdraw;
	}

	public void setCanWithdraw(boolean canWithdraw) {
		this.canWithdraw = canWithdraw;
	}

	public boolean isCanDeposit() {
		return canDeposit;
	}

	public void setCanDeposit(boolean canDeposit) {
		this.canDeposit = canDeposit;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public List<AssetBalanceFake> getBalances() {
		return balances;
	}

	public void setBalances(List<AssetBalanceFake> balances) {
		this.balances = balances;
	}

	/**
	 * Returns the asset balance for a given symbol.
	 *
	 * @param symbol asset symbol to obtain the balances from
	 * @return an asset balance for the given symbol which can be 0 in case the
	 *         symbol has no balance in the account
	 */
	public AssetBalanceFake getAssetBalance(String symbol) {
		for (AssetBalanceFake assetBalance : balances) {
			if (symbol.equals(assetBalance.getAsset())) {
				return assetBalance;
			}
		}
		return new AssetBalanceFake(symbol, "0", "0");
	}

	/**
	 * Set the asset balance for a given symbol.
	 *
	 * @param AssetBalanceFake asset to set the balances to
	 */
	public void setAssetBalance(AssetBalanceFake asset) {
		boolean found = false;
		if (balances == null) {
			balances = new ArrayList<AssetBalanceFake>();
		}
		for (AssetBalanceFake assetBalance : balances) {
			if (asset.getAsset().equals(assetBalance.getAsset())) {
				assetBalance.setFree(asset.getFree());
				assetBalance.setLocked(asset.getLocked());
				found = true;
			}
		}
		if (!found) {
			balances.add(asset);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)
				.append("makerCommission", makerCommission).append("takerCommission", takerCommission)
				.append("buyerCommission", buyerCommission).append("sellerCommission", sellerCommission)
				.append("canTrade", canTrade).append("canWithdraw", canWithdraw).append("canDeposit", canDeposit)
				.append("updateTime", updateTime).append("balances", balances).toString();
	}
}
