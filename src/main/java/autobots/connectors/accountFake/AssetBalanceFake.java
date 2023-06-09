package autobots.connectors.accountFake;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.binance.api.client.constant.BinanceApiConstants;

/**
 * An asset balance in an Account.
 *
 * @see AccountFake
 */
public class AssetBalanceFake {

	/**
	 * Asset symbol.
	 */
	private String asset;

	/**
	 * Available balance.
	 */
	private String free;

	/**
	 * Locked by open orders.
	 */
	private String locked;

	public AssetBalanceFake(String asset, String free, String locked) {
		super();
		this.asset = asset;
		this.free = free;
		this.locked = locked;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}

	public String getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE).append("asset", asset)
				.append("free", free).append("locked", locked).toString();
	}
}
