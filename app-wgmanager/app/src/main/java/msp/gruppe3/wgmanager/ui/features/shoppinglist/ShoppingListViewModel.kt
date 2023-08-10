package msp.gruppe3.wgmanager.ui.features.shoppinglist

import androidx.lifecycle.*
import msp.gruppe3.wgmanager.models.Item
import msp.gruppe3.wgmanager.models.ShoppingListEntry


class ShoppingListViewModel : ViewModel() {

    var shoppingLists = MutableLiveData<List<ShoppingListEntry>>()
    var items = MutableLiveData<List<Item>>()
}